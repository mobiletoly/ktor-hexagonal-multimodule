package com.github.mobiletoly.addrbookhexktor.core.service

import com.github.mobiletoly.addrbookhexktor.outport.GenerateRandomPersonPort
import com.github.mobiletoly.addrbookhexktor.usecase.AddAddrBookEntryUsecase
import com.github.mobiletoly.addrbookhexktor.usecase.AddressBookEntry
import com.github.mobiletoly.addrbookhexktor.usecase.PopulateRandomPersonUsecase

class RandomPersonService(
    private val randomPersonPort: GenerateRandomPersonPort,
    private var addAddrBookEntryPort: AddAddrBookEntryPort,
) : PopulateRandomPersonUsecase {

    override suspend fun populateRandomPerson(): AddressBookEntry {
        randomPersonPort.generateRandomPerson()
    }
}
