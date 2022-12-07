package adapters.env

import core.outport.GetDeploymentEnvPort

internal class AppOsEnvironment :
    GetDeploymentEnvPort {

    override val deploymentEnv: String by lazy {
        System.getenv(APP_DEPLOYMENT_ENV_KEY)
            ?: throw IllegalStateException("<$APP_DEPLOYMENT_ENV_KEY> environment variable is missing")
    }

    private val APP_DEPLOYMENT_ENV_KEY = "APP_DEPLOYMENT_ENV"
}
