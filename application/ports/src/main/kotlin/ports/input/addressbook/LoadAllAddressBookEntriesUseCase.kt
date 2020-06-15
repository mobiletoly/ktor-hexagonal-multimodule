package ports.input.addressbook

import ports.models.AddressBookEntry

interface LoadAllAddressBookEntriesUseCase {
    suspend fun loadAllAddressBookEntries(): List<AddressBookEntry>
}
