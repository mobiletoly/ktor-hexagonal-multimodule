package com.github.mobiletoly.addrbookhexktor.usecase

import com.github.mobiletoly.addrbookhexktor.outport.GetDeploymentConfigPort

interface GetHealthStatus {
    suspend fun execute(): HealthStatus
}

data class HealthStatus(
    val version: String,
    val databaseReady: Boolean,
    val remotePersonServiceReady: Boolean,
)

class GetHealthStatusImpl internal constructor(
    deploymentPort: GetDeploymentConfigPort,
) : GetHealthStatus {
    private val deployment = deploymentPort.deployment

    override suspend fun execute(): HealthStatus {
        return HealthStatus(
            version = "env=${deployment.env} version=${deployment.version} build=${deployment.buildNumber}",
            databaseReady = true,
            remotePersonServiceReady = true,
        )
    }
}
