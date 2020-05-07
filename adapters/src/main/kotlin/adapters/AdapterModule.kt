package adapters

import adapters.clients.randomperson.RandomPersonHttpClient
import adapters.config.AppConfig
import adapters.config.ConfigRepository
import adapters.config.ConfigRepositoryImpl
import adapters.config.EnvironmentVariables
import adapters.config.EnvironmentVariablesImpl
import adapters.db.DatabaseConnector
import adapters.db.DatabaseErrorInspector
import adapters.db.TransactionServiceDbImpl
import adapters.db.addressbook.AddressBookItemRepositoryDbImpl
import adapters.db.addressbook.PostalAddressRepositoryDbImpl
import adapters.db.postgresql.PgErrorInspector
import adapters.http.HttpClientFactory
import adapters.http.HttpClientFactoryImpl
import adapters.routes.AddressBookItemRoute
import adapters.routes.HealthCheckRoute
import adapters.services.healthcheck.HealthCheckService
import adapters.util.DateSupplierSystemTimeImpl
import ports.required.TransactionService
import ports.required.addressbook.AddressBookItemRepository
import ports.required.addressbook.PostalAddressRepository
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import org.koin.core.scope.Scope
import org.koin.dsl.module
import ports.required.util.DateSupplier
import ports.required.randomperson.RandomPersonClient
import javax.sql.DataSource

// Adapter modules for Dependency Injection

// Environment-specific configuration
val envModule = module(createdAtStart = true) {
    single<EnvironmentVariables> {
        EnvironmentVariablesImpl()
    }
    single<DateSupplier> {
        DateSupplierSystemTimeImpl()
    }
}

val adapterModule = module(createdAtStart = true) {
    // Configuration
    single<ConfigRepository> {
        ConfigRepositoryImpl(envVars = get())
    }
    single {
        AppConfig(configRepository = get())
    }
    single {
        AppBootstrap(application = getApplication())
    }

    // Database
    single<DataSource> {
        // Data source. We use 1 data source per 1 database. One data source may supply multiple connections.
        HikariDataSource(get<AppConfig>().hikari)
    }
    single {
        DatabaseConnector(dataSource = get())
    }
    single<TransactionService> {
        TransactionServiceDbImpl(dbConnector = get())
    }

    // Data repositories
    single<AddressBookItemRepository> {
        AddressBookItemRepositoryDbImpl(dbConnector = get())
    }
    single<PostalAddressRepository> {
        PostalAddressRepositoryDbImpl(dbConnector = get())
    }

    // Clients
    single<HttpClientFactory> {
        HttpClientFactoryImpl()
    }
    single<RandomPersonClient> {
        RandomPersonHttpClient(appConfig = get(), httpClientFactory = get())
    }

    // API adapters.routes
    single {
        HealthCheckRoute(application = getApplication())
    }
    single {
        AddressBookItemRoute(application = getApplication())
    }

    // Internal adapter services
    single {
        HealthCheckService(appConfig = get(), dateSupplier = get())
    }
    single<DatabaseErrorInspector> {
        PgErrorInspector()
    }
}

private fun Scope.getApplication() = getProperty("application") as Application
