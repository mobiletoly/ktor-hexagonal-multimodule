package ports.output.addressbook

import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry

/**
 * Port to generate random person and store it in Address Book.
 */
interface FetchRandomPersonPort {
    @RequiresTransactionContext
    suspend fun fetchRandomPerson(): AddressBookEntry
}
