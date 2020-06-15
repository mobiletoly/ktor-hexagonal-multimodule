package adapters.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

interface ConfigRepository {
    val config: Config
}

class ConfigRepositoryImpl(
    envVars: EnvironmentVariables
) : ConfigRepository {

    private val deployment = envVars.deployment

    override val config: Config by lazy {
        val envConfig = ConfigFactory.load("config-$deployment.conf")
        val commonConf = ConfigFactory.load("config-common.conf")
        ConfigFactory
            .load()
            .withFallback(envConfig)
            .withFallback(commonConf)
            .resolve()
    }
}
