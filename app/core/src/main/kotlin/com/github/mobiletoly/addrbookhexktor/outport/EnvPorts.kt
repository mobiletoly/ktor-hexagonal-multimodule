package com.github.mobiletoly.addrbookhexktor.outport

import java.net.URL
import java.util.Properties

interface GetDeploymentEnvPort {
    val deploymentEnv: String
}

interface GetDeploymentConfigPort {
    val deployment: Config

    data class Config(
        val env: String,
        val version: String,
        val buildNumber: String,
    )
}

interface GetDatabaseConfigPort {
    val database: Properties
}

interface GetRandomPersonServiceConfigPort {
    val randomPersonService: Config

    data class Config(
        val fetchUrl: URL,
    )
}
