package ports.input.addressbook

import ports.models.AddressBookEntry

interface PopulateRandomPersonUseCase {
    suspend fun populateRandomPerson(): AddressBookEntry
}
