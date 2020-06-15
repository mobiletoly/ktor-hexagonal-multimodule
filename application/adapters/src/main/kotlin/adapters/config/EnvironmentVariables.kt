package adapters.config

import java.lang.IllegalStateException

interface EnvironmentVariables {
    val deployment: String
}

class EnvironmentVariablesImpl : EnvironmentVariables {
    override val deployment: String by lazy {
        System.getenv(varDeployment) ?: throw IllegalStateException("<$varDeployment> environment variable is missing")
    }

    private val varDeployment = "APP_DEPLOYMENT_ENV"
}
