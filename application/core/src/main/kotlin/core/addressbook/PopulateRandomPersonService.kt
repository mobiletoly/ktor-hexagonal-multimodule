package core.addressbook

import ports.input.RequiresTransactionContext
import ports.input.TransactionService
import ports.input.addressbook.PopulateRandomPersonUseCase
import ports.models.AddressBookEntry
import ports.output.addressbook.FetchRandomPersonPort
import ports.output.addressbook.SaveAddressBookEntryPort

class PopulateRandomPersonService(
    private val txService: TransactionService,
    private val fetchRandomPersonPort: FetchRandomPersonPort,
    private val saveAddressBookEntryPort: SaveAddressBookEntryPort
) : PopulateRandomPersonUseCase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun populateRandomPerson(): AddressBookEntry = txService.transaction {
        val addressBookEntry = fetchRandomPersonPort.fetchRandomPerson()
        saveAddressBookEntryPort.addAddressBookEntry(addressBookEntry)
    }
}
