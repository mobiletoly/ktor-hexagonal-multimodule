package com.github.mobiletoly.addrbookhexktor.adapters.remoting.randomperson

import com.github.mobiletoly.addrbookhexktor.outport.GenerateRandomPersonPort
import com.github.mobiletoly.addrbookhexktor.outport.RandomPersonServiceConfig
import com.github.mobiletoly.addrbookhexktor.usecase.AddressBookEntry

class RandomPersonAdapter(
    config: RandomPersonServiceConfig,
) : GenerateRandomPersonPort {
    private val client = RandomPersonHttpClient(config)

    override suspend fun generateRandomPerson(): AddressBookEntry {
        val responseDto = client.fetchRandomPerson()
        return responseDto.toAddressBookEntry()
    }
}
