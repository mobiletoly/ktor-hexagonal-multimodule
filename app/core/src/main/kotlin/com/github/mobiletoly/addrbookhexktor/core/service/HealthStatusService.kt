package com.github.mobiletoly.addrbookhexktor.core.service

import com.github.mobiletoly.addrbookhexktor.outport.GetDeploymentConfigPort
import com.github.mobiletoly.addrbookhexktor.usecase.HealthStatus
import com.github.mobiletoly.addrbookhexktor.usecase.HealthStatusUsecase


class HealthStatusService internal constructor(
    deploymentPort: GetDeploymentConfigPort,
) : HealthStatusUsecase {
    private val deployment = deploymentPort.deployment

    override suspend fun healthStatus(): HealthStatus {
        return HealthStatus(
            version = "env=${deployment.env} version=${deployment.version} build=${deployment.buildNumber}",
            databaseReady = true,
            remotePersonServiceReady = true,
        )
    }
}
