package core.outport

import core.models.PersonEntry

interface GenerateRandomPersonPort {
    suspend fun generateRandomPerson(): PersonEntry
}
