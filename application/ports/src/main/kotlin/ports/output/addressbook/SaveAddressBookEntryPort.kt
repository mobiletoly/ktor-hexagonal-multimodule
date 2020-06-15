package ports.output.addressbook

import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry

/**
 * Port to update Address Book Item entities.
 */
interface SaveAddressBookEntryPort {
    @RequiresTransactionContext
    fun addAddressBookEntry(addressBookEntry: AddressBookEntry): AddressBookEntry

    @RequiresTransactionContext
    fun updateAddressBookEntry(addressBookEntry: AddressBookEntry): AddressBookEntry
}
