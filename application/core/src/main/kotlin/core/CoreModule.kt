package core

import core.addressbook.DeleteAddressBookEntryService
import core.addressbook.LoadAddressBookEntryService
import core.addressbook.PopulateRandomPersonService
import core.addressbook.SaveAddressBookEntryService
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import ports.input.addressbook.AddAddressBookEntryUseCase
import ports.input.addressbook.LoadAddressBookEntryByIdUseCase
import ports.input.addressbook.LoadAllAddressBookEntriesUseCase
import ports.input.addressbook.UpdateAddressBookEntryUseCase
import ports.input.addressbook.DeleteAddressBookEntryUseCase
import ports.input.addressbook.PopulateRandomPersonUseCase

// Core module for Dependency Injection
val coreModule = module(createdAtStart = true) {

    // Services for use cases

    single {
        SaveAddressBookEntryService(
            txService = get(),
            saveAddressBookEntryPort = get()
        )
    } binds arrayOf(
        AddAddressBookEntryUseCase::class,
        UpdateAddressBookEntryUseCase::class
    )

    single {
        LoadAddressBookEntryService(
            txService = get(),
            loadAddressBookEntryPort = get()
        )
    } binds arrayOf(
        LoadAddressBookEntryByIdUseCase::class,
        LoadAllAddressBookEntriesUseCase::class
    )

    single {
        DeleteAddressBookEntryService(
            txService = get(),
            deleteAddressBookEntryPort = get()
        )
    } bind DeleteAddressBookEntryUseCase::class

    single {
        PopulateRandomPersonService(
            txService = get(),
            fetchRandomPersonPort = get(),
            saveAddressBookEntryPort = get()
        )
    } bind PopulateRandomPersonUseCase::class
}
