package com.github.mobiletoly.addrbookhexktor.usecase

interface HealthStatusUsecase {
    suspend fun healthStatus(): HealthStatus
}

data class HealthStatus(
    val version: String,
    val databaseReady: Boolean,
    val remotePersonServiceReady: Boolean,
)
