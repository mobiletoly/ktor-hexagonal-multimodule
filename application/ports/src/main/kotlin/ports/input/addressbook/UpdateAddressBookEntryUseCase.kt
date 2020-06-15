package ports.input.addressbook

import ports.models.AddressBookEntry

interface UpdateAddressBookEntryUseCase {
    suspend fun updateAddressBookEntry(command: UpdateAddressBookEntryCommand): AddressBookEntry

    data class UpdateAddressBookEntryCommand(
        val addressBookEntry: AddressBookEntry
    )
}
