package core.addressbook

import ports.models.AddressBookEntry
import ports.input.RequiresTransactionContext
import ports.input.TransactionService
import ports.input.addressbook.AddAddressBookEntryUseCase
import ports.input.addressbook.AddAddressBookEntryUseCase.AddAddressBookEntryCommand
import ports.input.addressbook.UpdateAddressBookEntryUseCase
import ports.input.addressbook.UpdateAddressBookEntryUseCase.UpdateAddressBookEntryCommand
import ports.output.addressbook.SaveAddressBookEntryPort

/**
 * Business logic to save (add, update) address book entries.
 */
class SaveAddressBookEntryService(
    private val txService: TransactionService,
    private val saveAddressBookEntryPort: SaveAddressBookEntryPort
) : AddAddressBookEntryUseCase,
    UpdateAddressBookEntryUseCase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun addAddressBookEntry(
        command: AddAddressBookEntryCommand
    ): AddressBookEntry = txService.transaction {
        saveAddressBookEntryPort.addAddressBookEntry(
            addressBookEntry = command.addressBookEntry
        )
    }

    @OptIn(RequiresTransactionContext::class)
    override suspend fun updateAddressBookEntry(
        command: UpdateAddressBookEntryCommand
    ): AddressBookEntry = txService.transaction {
        saveAddressBookEntryPort.updateAddressBookEntry(
            addressBookEntry = command.addressBookEntry
        )
    }
}
