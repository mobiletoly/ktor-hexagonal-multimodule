package core.usecase

import core.models.PersonEntry

interface PopulateRandomPersonUsecase {
    suspend fun populateRandomPerson(): PersonEntry
}
