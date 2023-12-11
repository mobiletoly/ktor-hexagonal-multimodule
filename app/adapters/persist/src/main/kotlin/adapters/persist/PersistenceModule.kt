package adapters.persist

import adapters.persist.addressbook.LoadPersonAdapter
import adapters.persist.addressbook.SavePersonAdapter
import adapters.persist.addressbook.repo.PersonRepo
import adapters.persist.addressbook.repo.PostalAddressRepo
import adapters.persist.util.DatabaseErrorInspector
import adapters.persist.util.postgresql.PgErrorInspector
import core.outport.AddPersonPort
import core.outport.BootPersistStoragePort
import core.outport.DeletePersonPort
import core.outport.GetDatabaseConfigPort
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import core.outport.PersistTransactionPort
import core.outport.ShutdownPersistStoragePort
import core.outport.UpdatePersonPort
import org.koin.dsl.binds
import org.koin.dsl.module

val persistenceModule =
    module {
        single<DatabaseErrorInspector> {
            PgErrorInspector()
        }

        single {
            DatabaseConnector(
                databaseConfig = get<GetDatabaseConfigPort>().database,
                errorInspector = get(),
            )
        } binds arrayOf(
            BootPersistStoragePort::class,
            ShutdownPersistStoragePort::class,
            PersistTransactionPort::class,
        )

        single {
            PersonRepo()
        }
        single {
            PostalAddressRepo()
        }

        single {
            LoadPersonAdapter(
                personRepo = get(),
                postalAddressRepo = get(),
            )
        } binds arrayOf(
            LoadPersonPort::class,
            LoadAllPersonsPort::class,
        )
        single {
            SavePersonAdapter(
                personRepository = get(),
                postalAddressRepo = get(),
            )
        } binds arrayOf(
            AddPersonPort::class,
            UpdatePersonPort::class,
            DeletePersonPort::class,
        )
    }
