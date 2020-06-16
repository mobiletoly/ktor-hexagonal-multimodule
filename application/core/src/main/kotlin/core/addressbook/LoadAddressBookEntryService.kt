package core.addressbook

import ports.input.RequiresTransactionContext
import ports.input.TransactionService
import ports.input.addressbook.LoadAddressBookEntryByIdUseCase
import ports.input.addressbook.LoadAddressBookEntryByIdUseCase.LoadAddressBookEntryByIdUseCaseCommand
import ports.input.addressbook.LoadAllAddressBookEntriesUseCase
import ports.models.AddressBookEntry
import ports.output.addressbook.LoadAddressBookEntryPort

/**
 * Business logic to query address book entries.
 */
class LoadAddressBookEntryService(
    private val txService: TransactionService,
    private val loadAddressBookEntryPort: LoadAddressBookEntryPort
) : LoadAddressBookEntryByIdUseCase,
    LoadAllAddressBookEntriesUseCase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun loadAddressBookEntryById(
        command: LoadAddressBookEntryByIdUseCaseCommand
    ): AddressBookEntry = txService.transaction {
        loadAddressBookEntryPort.loadAddressBookEntry(command.id)
    }

    @OptIn(RequiresTransactionContext::class)
    override suspend fun loadAllAddressBookEntries(): List<AddressBookEntry> = txService.transaction {
        loadAddressBookEntryPort.loadAllAddressBookEntries()
    }
}
