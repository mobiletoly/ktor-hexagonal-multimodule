package adapters.primary.web.routes.healthcheck

import adapters.services.healthcheck.HealthCheckService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

class HealthCheckRoute(application: Application) {

    private val healthCheckService by application.inject<HealthCheckService>()

    init {
        application.routing {

            get("/health") {
                val healthCheck = healthCheckService.status()
                call.respond(HttpStatusCode.OK, healthCheck)
            }
        }
    }
}
