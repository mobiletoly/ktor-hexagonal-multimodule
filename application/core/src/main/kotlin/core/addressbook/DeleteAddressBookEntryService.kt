package core.addressbook

import ports.input.RequiresTransactionContext
import ports.input.TransactionService
import ports.input.addressbook.DeleteAddressBookEntryUseCase
import ports.input.addressbook.DeleteAddressBookEntryUseCase.DeleteAddressBookEntryUseCaseCommand
import ports.output.addressbook.DeleteAddressBookEntryPort

/**
 * Business logic to save (add, update) address book entries.
 */
class DeleteAddressBookEntryService(
    private val txService: TransactionService,
    private val deleteAddressBookEntryPort: DeleteAddressBookEntryPort
) : DeleteAddressBookEntryUseCase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun deleteAddressBookEntry(
        command: DeleteAddressBookEntryUseCaseCommand
    ) = txService.transaction {
        deleteAddressBookEntryPort.deleteAddressBookEntry(id = command.id)
    }
}
