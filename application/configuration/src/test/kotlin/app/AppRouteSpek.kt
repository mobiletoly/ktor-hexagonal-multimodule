package app

import adapters.adapterModule
import adapters.persistence.DatabaseConnector
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import core.coreModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import org.koin.core.context.KoinContextHandler
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.koin.logger.SLF4JLogger
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Root
import org.testcontainers.containers.PostgreSQLContainer
import ports.input.RequiresTransactionContext
import ports.input.StartsNewTransaction
import java.util.Properties
import javax.sql.DataSource

/**
 * Derive your objects from this class if you want to initialize TestContainers database
 * and test REST interfaces.
 */
abstract class AppRouteSpek(val appRoot: Root.() -> Unit) : Spek({

    beforeGroup {
        if (! postgresContainer.isRunning) {
            postgresContainer.start()
        }
    }

    appRoot()
}) {
    companion object {
        class AppPostgreSQLContainer : PostgreSQLContainer<AppPostgreSQLContainer>()

        val postgresContainer = AppPostgreSQLContainer()

        @StartsNewTransaction
        fun <R> withApp(test: suspend TestApplicationEngine.() -> R) = withTestApplication {
            runBlocking {
                initApp(application)
                try {
                    test.invoke(this@withTestApplication)
                } finally {
                    // Clean-up after each test
                    val dbConnector: DatabaseConnector = application.get()
                    @OptIn(RequiresTransactionContext::class) dbConnector.newTransaction {
                        dbConnector.deleteAllTables()
                    }
                    val dataSource: DataSource = application.get()
                    (dataSource as HikariDataSource).close()
                    KoinContextHandler.stop()
                }
            }
        }

        private fun initApp(application: Application) {
            System.setProperty("APP_DEPLOYMENT_ENV", "test")
            System.setProperty("APP_VERSION", "0.0")
            System.setProperty("APP_BUILD_NUMBER", "0")
            System.setProperty("APP_DB_USERNAME", postgresContainer.username)
            System.setProperty("APP_DB_PASSWORD", postgresContainer.password)
            System.setProperty("APP_DB_URI", postgresContainer.jdbcUrl)
            val mainConfigProperties = Properties().apply {
                // Add any test-specific properties here to be merged with config-common.conf, e.g.
                //    put("app-config.main-db.hikari.autoCommit", true)
            }
            val config = ConfigFactory.parseProperties(mainConfigProperties)
            KoinContextHandler.stop()
            application.install(Koin) {
                SLF4JLogger()
                modules(
                    module {
                        single { application }
                        single { config }
                    },
                    envTestModule,
                    adapterModule,
                    coreModule
                )
            }
        }
    }
}
