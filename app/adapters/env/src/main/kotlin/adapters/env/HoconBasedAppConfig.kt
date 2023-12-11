package adapters.env

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import core.outport.DeploymentConfig
import core.outport.GetDatabaseConfigPort
import core.outport.GetDeploymentConfigPort
import core.outport.GetRandomPersonServiceConfigPort
import core.outport.RandomPersonServiceConfig
import java.util.Properties

/**
 * Application configuration reader from HOCON config file.
 */
internal class HoconBasedAppConfig(deploymentEnv: String) :
    GetDeploymentConfigPort,
    GetDatabaseConfigPort,
    GetRandomPersonServiceConfigPort {
    private val config: Config

    init {
        val envConfig = ConfigFactory.load("config-$deploymentEnv.conf")
        val commonConf = ConfigFactory.load("config-common.conf")
        val rootConfig =
            ConfigFactory
                .load()
                .withFallback(envConfig)
                .withFallback(commonConf)
                .resolve()
        this.config = rootConfig.getConfig("app-config")
    }

    override val deployment: DeploymentConfig by lazy {
        val node = config.getConfig("deployment")
        DeploymentConfig(
            env = node.getString("env"),
            version = node.getString("version"),
            buildNumber = node.getString("buildNumber"),
        )
    }

    override val database: Properties by lazy {
        val node = config.getConfig("main-db.hikari")
        node.toProperties()
    }

    override val randomPersonService: RandomPersonServiceConfig by lazy {
        val node = config.getConfig("random-person-service")
        RandomPersonServiceConfig(
            fetchUrl = node.getString("fetch-url"),
            apiKey = node.getString("api-key"),
        )
    }

    private fun Config.toProperties() =
        Properties().also {
            for (e in this.entrySet()) {
                it.setProperty(e.key, this.getString(e.key))
            }
        }
}
