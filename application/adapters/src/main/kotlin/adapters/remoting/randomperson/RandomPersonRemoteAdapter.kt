package adapters.remoting.randomperson

import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry
import ports.output.addressbook.FetchRandomPersonPort

internal class RandomPersonRemoteAdapter(
    private val randomPersonHttpClient: RandomPersonHttpClient
) : FetchRandomPersonPort {

    @RequiresTransactionContext
    override suspend fun fetchRandomPerson(): AddressBookEntry {
        val responseDto = randomPersonHttpClient.fetchRandomPerson()
        return responseDto.toAddressBookEntry()
    }
}
