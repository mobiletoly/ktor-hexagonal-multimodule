package core.services

import com.github.michaelbull.logging.InlineLogger
import core.models.PersonEntry
import core.outport.AddPersonPort
import core.outport.GenerateRandomPersonPort
import core.outport.PersistTransactionPort
import core.outport.RequiresTransactionContext
import core.usecase.PopulateRandomPersonUsecase

class RandomPersonService(
    private val generateRandomPersonPort: GenerateRandomPersonPort,
    private val txPort: PersistTransactionPort,
    private val addPersonPort: AddPersonPort,
) : PopulateRandomPersonUsecase {

    private val logger = InlineLogger()

    @OptIn(RequiresTransactionContext::class)
    override suspend fun populateRandomPerson(): PersonEntry {
        logger.info { "populateRandomPerson(): Populate random person" }
        val entry = generateRandomPersonPort.generateRandomPerson()
        return txPort.withNewTransaction {
            addPersonPort.addPerson(entry)
        }
    }
}
