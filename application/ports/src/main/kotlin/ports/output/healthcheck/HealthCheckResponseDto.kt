package ports.output.healthcheck

import java.util.Date

data class HealthCheckResponseDto(
    val ready: Boolean,
    val env: String,
    val appVersion: String,
    val appBuildNumber: String,
    val responseTimestamp: Date
)
