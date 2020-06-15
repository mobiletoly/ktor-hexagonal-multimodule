package adapters.config

import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import java.util.Properties

/**
 * Central repository of all application's settings.
 */
class AppConfig(configRepository: ConfigRepository) {

    // you can keep your application-specific data in main.conf
    // "application.conf" is already reserved by ktor and we don't want to keep our configs in there,
    // otherwise we are going to have more complicated code

    private val config = configRepository.config.getConfig("app-config")

    val deployment by lazy {
        Deployment.create(config = config.getConfig("deployment"))
    }

    /**
     * Loads a HikariCP config specified in /resources/application.conf
     * or any other .conf files visible to HOCON.
     */
    val hikari by lazy {
        val dbConfig = config.getConfig("main-db.hikari")
        val props = dbConfig.toProperties()
        val hikariConfig = HikariConfig(props)
        hikariConfig
    }

    val randomPerson by lazy {
        RandomPerson.create(config = config.getConfig("random-person"))
    }

    class Deployment private constructor(private val config: Config) {
        val env: String by lazy {
            config.getString("env")
        }
        val version: String by lazy {
            config.getString("version")
        }
        val buildNumber: String by lazy {
            config.getString("buildNumber")
        }
        companion object {
            internal fun create(config: Config) = Deployment(config = config)
        }
    }

    class RandomPerson private constructor(private val config: Config) {
        val fetchUrl: String by lazy {
            config.getString("fetch-url")
        }
        val apiKey: String by lazy {
            config.getString("api-key")
        }
        companion object {
            internal fun create(config: Config) = RandomPerson(config = config)
        }
    }
}

// Convert HOCON Config object into Properties (some libraries, e.g. HikariCP don't understand HOCON format).
private fun Config.toProperties() = Properties().also {
    for (e in this.entrySet()) {
        it.setProperty(e.key, this.getString(e.key))
    }
}
