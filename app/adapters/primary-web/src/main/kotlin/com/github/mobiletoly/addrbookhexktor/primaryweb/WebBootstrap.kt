package com.github.mobiletoly.addrbookhexktor.primaryweb

import com.github.michaelbull.logging.InlineLogger
import com.github.mobiletoly.addrbookhexktor.primaryweb.routes.health.healthRoute
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.response.header
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import java.util.UUID

private const val xRequestIdLogKey = "xRequestId"
private val logger = InlineLogger()

fun Application.webBootstrap() {

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc(xRequestIdLogKey)
    }

    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate {
            val requestId = it.request.header(HttpHeaders.XRequestId)
            if (requestId.isNullOrEmpty()) {
                "${UUID.randomUUID()}"
            } else {
                requestId
            }
        }
        verify { callId: String ->
            callId.isNotEmpty()
        }
        // Allows to process the call to modify headers or generate a request from the callId
        reply { call: ApplicationCall, callId: String ->
            call.response.header(HttpHeaders.XRequestId, callId)
        }
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

    routing {
        trace {
            logger.debug {"routing/trace(): ${it.buildText()}" }
        }
        healthRoute()
    }
}
