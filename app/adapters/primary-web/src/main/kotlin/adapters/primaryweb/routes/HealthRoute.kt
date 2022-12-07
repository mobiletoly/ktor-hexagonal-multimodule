package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.HealthResponse
import core.outport.GetDeploymentConfigPort
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

internal fun Routing.healthRoute() {
    val deployment = inject<GetDeploymentConfigPort>().value.deployment

    get("/health") {
        call.respond(
            status = HttpStatusCode.OK,
            message = HealthResponse(
                status = "success",
                version = "env=${deployment.env} version=${deployment.version} build=${deployment.buildNumber}",
                remoteService = "success",
                database = "success",
            )
        )
    }
}
