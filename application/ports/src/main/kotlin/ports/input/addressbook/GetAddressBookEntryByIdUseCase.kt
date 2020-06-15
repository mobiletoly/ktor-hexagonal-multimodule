package ports.input.addressbook

import ports.models.AddressBookEntry

interface GetAddressBookEntryByIdUseCase {
    suspend fun addAddressBookEntry(command: AddAddressBookEntryCommand): AddressBookEntry

    data class AddAddressBookEntryCommand(
        val addressBookEntry: AddressBookEntry
    )
}
