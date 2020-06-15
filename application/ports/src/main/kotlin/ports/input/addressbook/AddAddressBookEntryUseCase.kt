package ports.input.addressbook

import ports.models.AddressBookEntry

interface AddAddressBookEntryUseCase {
    suspend fun addAddressBookEntry(command: AddAddressBookEntryCommand): AddressBookEntry

    data class AddAddressBookEntryCommand(
        val addressBookEntry: AddressBookEntry
    )
}
