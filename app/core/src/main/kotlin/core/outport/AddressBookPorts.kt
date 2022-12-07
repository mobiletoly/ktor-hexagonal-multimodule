package core.outport

import core.models.PersonEntry

interface AddPersonPort {
    fun addPerson(entry: PersonEntry): PersonEntry
}

interface LoadPersonPort {
    fun loadPerson(id: Long): PersonEntry
}

interface DeletePersonPort {
    fun deletePerson(id: Long)
}

interface UpdatePersonPort {
    fun updatePerson(entry: PersonEntry): PersonEntry
}

interface LoadAllPersonsPort {
    fun loadAllPersons(): Collection<PersonEntry>
}
