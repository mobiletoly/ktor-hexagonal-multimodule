package ports.output.addressbook

import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry

/**
 * Port to load Address Book Item entities.
 */
interface LoadAddressBookEntryPort {
    @RequiresTransactionContext
    fun loadAddressBookEntry(id: Long): AddressBookEntry

    @RequiresTransactionContext
    fun loadAllAddressBookEntries(): List<AddressBookEntry>
}
