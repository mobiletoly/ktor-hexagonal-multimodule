package adapters.primaryweb

import adapters.primaryweb.routes.healthRoute
import adapters.primaryweb.routes.personRoute
import adapters.primaryweb.util.RestGenericException
import adapters.primaryweb.util.RestInternalServerError
import adapters.primaryweb.util.respondRestException
import com.github.michaelbull.logging.InlineLogger
import common.log.setXRequestId
import common.log.X_REQUEST_ID_LOG_KEY
import core.errors.DomainException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import java.util.UUID

private val logger = InlineLogger()

fun Application.webBootstrap() {
    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
        mdc(X_REQUEST_ID_LOG_KEY) { call ->
            call.response.headers[HttpHeaders.XRequestId]
        }
    }

    install(CallId) {
        generate {
            val requestId = it.request.header(HttpHeaders.XRequestId)
            if (requestId.isNullOrEmpty()) {
                "${UUID.randomUUID()}"
            } else {
                requestId
            }
        }
        replyToHeader(HttpHeaders.XRequestId)
    }

    install(CORS) {
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
    }

    // Return proper HTTP error: https://ktor.io/features/status-pages.html
    // In this block we are mapping Domain and Adapter exceptions into proper HTTP error response.
    install(StatusPages) {
        exception<Exception> { call, e ->
            setXRequestId(call.callId)
            logger.error(e) { "StatusPages/exception(): Error to be returned to a caller" }
            when (e) {
                is DomainException -> {
                    val errorResponse = e.toRestGenericException().toRestErrorResponse(
                        path = call.request.uri,
                    )
                    call.respond(
                        status = HttpStatusCode.fromValue(errorResponse.status),
                        message = errorResponse,
                    )
                }

                is RestGenericException -> {
                    call.respondRestException(e)
                }

                else -> {
                    call.respondRestException(
                        RestInternalServerError(detail = e.message ?: e.toString()),
                    )
                }
            }
        }
    }

    routing {
        trace {
            logger.debug { "routing/trace(): ${it.buildText()}" }
        }
        healthRoute()
        personRoute()
    }
}
