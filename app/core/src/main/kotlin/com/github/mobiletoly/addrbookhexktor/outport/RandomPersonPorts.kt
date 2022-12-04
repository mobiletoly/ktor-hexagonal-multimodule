package com.github.mobiletoly.addrbookhexktor.outport

import com.github.mobiletoly.addrbookhexktor.usecase.AddressBookEntry

interface GenerateRandomPersonPort {
    suspend fun generateRandomPerson(): AddressBookEntry
}
