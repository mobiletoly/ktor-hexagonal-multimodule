package adapters.remoting.randomperson

import core.models.PersonEntry
import core.outport.GenerateRandomPersonPort
import core.outport.RandomPersonServiceConfig

class RandomPersonAdapter(
    config: RandomPersonServiceConfig,
) : GenerateRandomPersonPort {

    private val client = RandomPersonHttpClient(config)

    override suspend fun generateRandomPerson(): PersonEntry {
        val responseDto = client.fetchRandomPerson()
        return responseDto.toAddressBookEntry()
    }
}
