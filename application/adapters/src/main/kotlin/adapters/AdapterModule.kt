package adapters

import adapters.remoting.randomperson.RandomPersonHttpClient
import adapters.config.AppConfig
import adapters.config.ConfigRepository
import adapters.config.ConfigRepositoryImpl
import adapters.config.EnvironmentVariables
import adapters.config.EnvironmentVariablesImpl
import adapters.persistence.DatabaseConnector
import adapters.persistence.DatabaseErrorInspector
import adapters.persistence.TransactionServiceDbImpl
import adapters.persistence.addressbook.PostalAddressRepository
import adapters.persistence.util.postgresql.PgErrorInspector
import adapters.remoting.HttpClientFactory
import adapters.remoting.HttpClientFactoryImpl
import adapters.persistence.addressbook.AddressBookPersistenceAdapter
import adapters.persistence.addressbook.AddressBookItemRepository
import adapters.remoting.randomperson.RandomPersonRemoteAdapter
import adapters.primary.web.routes.healthcheck.HealthCheckRoute
import adapters.services.healthcheck.HealthCheckService
import adapters.util.DateSupplierSystemTimeImpl
import adapters.primary.web.routes.addressbook.DeleteAddressBookEntryRoute
import adapters.primary.web.routes.addressbook.LoadAddressBookEntryRoute
import adapters.primary.web.routes.addressbook.PopulateRandomPersonEntryRoute
import adapters.primary.web.routes.addressbook.SaveAddressBookEntryRoute
import ports.input.TransactionService
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import ports.output.addressbook.LoadAddressBookEntryPort
import ports.input.util.DateSupplier
import ports.output.addressbook.DeleteAddressBookEntryPort
import ports.output.addressbook.FetchRandomPersonPort
import ports.output.addressbook.SaveAddressBookEntryPort
import javax.sql.DataSource

// Environment-specific configuration
val envModule = module(createdAtStart = true) {
    single<EnvironmentVariables> {
        EnvironmentVariablesImpl()
    }
    single<DateSupplier> {
        DateSupplierSystemTimeImpl()
    }
}

// Adapter modules
val adapterModule = module(createdAtStart = true) {
    // Configuration
    single<ConfigRepository> {
        ConfigRepositoryImpl(envVars = get())
    }
    single {
        AppConfig(configRepository = get())
    }
    single {
        AppBootstrap(application = get())
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
    single {
        AddressBookItemRepository()
    }
    single {
        PostalAddressRepository()
    }
    // Data adapters
    single {
        AddressBookPersistenceAdapter(addressBookItemRepository = get(), postalAddressRepository = get())
    } binds arrayOf(
        DeleteAddressBookEntryPort::class,
        LoadAddressBookEntryPort::class,
        SaveAddressBookEntryPort::class
    )

    // Remote clients
    single<HttpClientFactory> {
        HttpClientFactoryImpl()
    }
    single {
        RandomPersonHttpClient(appConfig = get(), httpClientFactory = get())
    }
    // Remote adapters
    single {
        RandomPersonRemoteAdapter(randomPersonHttpClient = get())
    } bind FetchRandomPersonPort::class

    // Web routes
    single { HealthCheckRoute(application = get()) }
    single { SaveAddressBookEntryRoute(application = get()) }
    single { LoadAddressBookEntryRoute(application = get()) }
    single { DeleteAddressBookEntryRoute(application = get()) }
    single { PopulateRandomPersonEntryRoute(application = get()) }

    // Internal adapter services
    single {
        HealthCheckService(appConfig = get(), dateSupplier = get())
    }
    single<DatabaseErrorInspector> {
        PgErrorInspector()
    }
}
