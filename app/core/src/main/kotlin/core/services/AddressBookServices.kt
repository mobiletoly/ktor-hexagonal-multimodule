package core.services

import com.github.michaelbull.logging.InlineLogger
import core.models.PersonEntry
import core.outport.AddPersonPort
import core.outport.DeletePersonPort
import core.outport.GenerateRandomPersonPort
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import core.outport.PersistTransactionPort
import core.outport.UpdatePersonPort
import core.usecase.AddPersonUsecase
import core.usecase.DeletePersonUsecase
import core.usecase.LoadAllPersonsUsecase
import core.usecase.LoadPersonUsecase
import core.usecase.PopulateRandomPersonUsecase
import core.usecase.UpdatePersonUsecase

internal class AddPersonService(
    private val addPersonPort: AddPersonPort,
    private val txPort: PersistTransactionPort,
) : AddPersonUsecase {

    override suspend fun addPerson(entry: PersonEntry) = txPort.withNewTransaction {
        addPersonPort.addPerson(entry)
    }
}

internal class LoadPersonService(
    private val loadPersonPort: LoadPersonPort,
    private val txPort: PersistTransactionPort,
) : LoadPersonUsecase {

    override suspend fun loadPerson(id: Long) = txPort.withNewTransaction {
        loadPersonPort.loadPerson(id)
    }
}

internal class DeletePersonService(
    private val deletePersonPort: DeletePersonPort,
    private val txPort: PersistTransactionPort,
) : DeletePersonUsecase {

    override suspend fun deletePerson(id: Long) = txPort.withNewTransaction {
        deletePersonPort.deletePerson(id)
    }
}

internal class UpdatePersonService(
    private val updatePersonPort: UpdatePersonPort,
    private val txPort: PersistTransactionPort,
) : UpdatePersonUsecase {

    override suspend fun updatePerson(entry: PersonEntry) = txPort.withNewTransaction {
        updatePersonPort.updatePerson(entry)
    }
}

internal class LoadAllPersonsService(
    private val loadAllPersonsPort: LoadAllPersonsPort,
    private val txPort: PersistTransactionPort,
) : LoadAllPersonsUsecase {

    override suspend fun loadAllPersons() = txPort.withNewTransaction {
        loadAllPersonsPort.loadAllPersons()
    }
}

internal class RandomPersonService(
    private val generateRandomPersonPort: GenerateRandomPersonPort,
    private val addPersonPort: AddPersonPort,
    private val txPort: PersistTransactionPort,
) : PopulateRandomPersonUsecase {

    private val logger = InlineLogger()

    override suspend fun populateRandomPerson(): PersonEntry {
        logger.info { "populateRandomPerson(): Populate random person and save it into persistent storage" }
        val entry = generateRandomPersonPort.generateRandomPerson()
        return txPort.withNewTransaction {
            addPersonPort.addPerson(entry)
        }
    }
}
