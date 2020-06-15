package ports.output.addressbook

import ports.input.RequiresTransactionContext

/**
 * Port to load Address Book Item entities.
 */
interface DeleteAddressBookEntryPort {
    @RequiresTransactionContext
    fun deleteAddressBookEntry(id: Long)
}
