package com.github.mobiletoly.addrbookhexktor.primaryweb.routes.health

import com.github.mobiletoly.addrbookhexktor.primaryweb.models.HealthResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

internal fun Routing.healthRoute() {
    get("/health") {
        call.respond(
            status = HttpStatusCode.OK,
            message = HealthResponse(
                status = "success",
                remoteService = "success",
                database = "success",
            )
        )
    }
}
