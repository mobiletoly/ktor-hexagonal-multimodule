package adapters

import adapters.persistence.DatabaseErrorInspector
import adapters.primary.web.util.RestGenericException
import adapters.primary.web.util.respondRestException
import adapters.primary.web.util.throwRestException
import adapters.primary.web.util.toRestErrorResponse
import adapters.util.setProjectDefaults
import com.github.michaelbull.logging.InlineLogger
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.callIdMdc
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.ktor.ext.inject
import ports.output.errors.DomainException
import shared.util.e
import java.util.UUID

class AppBootstrap(
    application: Application
) {
    private val logger = InlineLogger()

    init {
        val databaseErrorInspector by application.inject<DatabaseErrorInspector>()

        application.apply {
            install(AutoHeadResponse)

            // ktor 0.9.5 added MDC support for coroutines and this allows us to print call request id for the entire
            // execution context. This is great, because we can return that call request id back to a client
            // in a header and in case of error, user can provide us with a call request id (let's say we might
            // print it on a screen or in JavaScript console) and we could track the entire execution path even
            // in a very busy logs (e.g. on file system or in Splunk).
            // In order to print call request id we use %X{mdc-callid} specifier in resources/logback.xml
            install(CallLogging) {
                callIdMdc("mdc-callid")
            }
            install(CallId) {
                // Unique id will be generated in form of "callid-UUID" for a CallLogging feature described above
                generate {
                    val requestId = it.request.header(HttpHeaders.XRequestId)
                    if (requestId.isNullOrEmpty()) {
                        "${UUID.randomUUID()}"
                    } else {
                        requestId
                    }
                }
                retrieve { call ->
                    call.request.header(HttpHeaders.XRequestId)
                }
                verify { callId: String ->
                    callId.isNotEmpty()
                }
                // Allows to process the call to modify headers or generate a request from the callId
                reply { call: ApplicationCall, callId: String ->
                    call.response.header(HttpHeaders.XRequestId, callId)
                }
            }

            // Some frameworks such as Angular require additional CORS configuration
            install(CORS) {
                method(HttpMethod.Options)
                method(HttpMethod.Put)
                method(HttpMethod.Delete)
                header("*")
                allowCredentials = true
                allowSameOrigin = true
                anyHost()
            }

            // Content conversions - here we setup serialization and deserialization of JSON objects
            install(ContentNegotiation) {
                // We use Jackson for JSON: https://github.com/FasterXML/jackson
                jackson {
                    setProjectDefaults()
                }
            }

            // Return proper HTTP error: https://ktor.io/features/status-pages.html
            // In this block we are mapping Domain and Adapter exceptions into proper HTTP error response.
            install(StatusPages) {
                exception<DomainException> { ex ->
                    logger.e("StatusPages/DomainException", ex) { "REST error to be returned to a caller" }
                    val errorResponse = ex.toRestErrorResponse(path = call.request.uri)
                    call.respond(
                        status = HttpStatusCode.fromValue(errorResponse.status),
                        message = errorResponse
                    )
                }
                exception<RestGenericException> { ex ->
                    logger.e("StatusPages/RestGenericException", ex) { "REST error to be returned to a caller" }
                    call.respondRestException(ex)
                }
                exception<ExposedSQLException> { ex ->
                    logger.e("StatusPages/ExposedSQLException", ex) { "REST error to be returned to a caller" }
                    try {
                        ex.throwRestException(databaseErrorInspector)
                    } catch (ex: RestGenericException) {
                        call.respondRestException(ex)
                    }
                }
            }
        }
    }
}
