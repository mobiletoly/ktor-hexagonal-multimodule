package ports.input.addressbook

import ports.models.AddressBookEntry

interface LoadAddressBookEntryByIdUseCase {
    suspend fun loadAddressBookEntryById(command: LoadAddressBookEntryByIdUseCaseCommand): AddressBookEntry

    data class LoadAddressBookEntryByIdUseCaseCommand(
        val id: Long
    )
}
