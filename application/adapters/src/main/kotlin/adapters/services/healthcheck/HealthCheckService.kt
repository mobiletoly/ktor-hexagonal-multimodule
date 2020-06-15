package adapters.services.healthcheck

import adapters.config.AppConfig
import ports.output.healthcheck.HealthCheckResponseDto
import ports.input.util.DateSupplier
import java.util.Date

class HealthCheckService(
    private val appConfig: AppConfig,
    private val dateSupplier: DateSupplier
) {
    fun status(): HealthCheckResponseDto {
        return HealthCheckResponseDto(
            ready = true,
            env = appConfig.deployment.env,
            appVersion = appConfig.deployment.version,
            appBuildNumber = appConfig.deployment.buildNumber,
            responseTimestamp = Date(dateSupplier.currentTimeMillis())
        )
    }
}
