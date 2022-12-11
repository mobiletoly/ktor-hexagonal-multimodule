package core.services

import core.outport.GetDeploymentConfigPort
import core.usecase.HealthStatus
import core.usecase.HealthStatusUsecase

internal class HealthStatusService internal constructor(
    getDeploymentPort: GetDeploymentConfigPort,
) : HealthStatusUsecase {
    private val deployment = getDeploymentPort.deployment

    override suspend fun healthStatus(): HealthStatus {
        return HealthStatus(
            version = "env=${deployment.env} version=${deployment.version} build=${deployment.buildNumber}",
            databaseReady = true,
            remotePersonServiceReady = true,
        )
    }
}
