package core.usecase

import core.models.PersonEntry

fun interface AddPersonUsecase {
    suspend fun addPerson(entry: PersonEntry): PersonEntry
}

fun interface LoadPersonUsecase {
    suspend fun loadPerson(id: Long): PersonEntry
}

fun interface DeletePersonUsecase {
    suspend fun deletePerson(id: Long)
}

fun interface UpdatePersonUsecase {
    suspend fun updatePerson(entry: PersonEntry): PersonEntry
}

fun interface LoadAllPersonsUsecase {
    suspend fun loadAllPersons(): Collection<PersonEntry>
}
