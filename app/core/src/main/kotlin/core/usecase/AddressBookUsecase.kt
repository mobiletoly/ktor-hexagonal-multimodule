package core.usecase

import core.models.PersonEntry

interface AddPersonUsecase {
    suspend fun addPerson(entry: PersonEntry): PersonEntry
}

interface LoadPersonUsecase {
    suspend fun loadPerson(id: Long): PersonEntry
}

interface DeletePersonUsecase {
    suspend fun deletePerson(id: Long)
}

interface UpdatePersonUsecase {
    suspend fun updatePerson(entry: PersonEntry): PersonEntry
}

interface LoadAllPersonsUsecase {
    suspend fun loadAllPersons(): Collection<PersonEntry>
}
