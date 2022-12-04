package com.github.mobiletoly.addrbookhexktor.usecase

interface PopulateRandomPersonUsecase {
    suspend fun populateRandomPerson(): AddressBookEntry
}
