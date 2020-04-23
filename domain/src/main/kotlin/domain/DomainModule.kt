package domain

import domain.addressbook.AddressBookServiceImpl
import ports.provided.addressbook.AddressBookService
import org.koin.dsl.module

// Domain module for Dependency Injection
val domainModule = module(createdAtStart = true) {
    single<AddressBookService> {
        AddressBookServiceImpl(
            addressBookItemRepository = get(),
            postalAddressRepository = get(),
            txService = get()
        )
    }
}
