package adapters.env

import core.outport.GetDeploymentEnvPort

internal class AppOsEnvironment :
    GetDeploymentEnvPort {

    override val deploymentEnv: String by lazy {
        System.getenv(appDeploymentEnvKey)
            ?: throw IllegalStateException("<$appDeploymentEnvKey> environment variable is missing")
    }

    private val appDeploymentEnvKey = "APP_DEPLOYMENT_ENV"
}
