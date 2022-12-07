package core.services

import core.models.PersonEntry
import core.outport.AddPersonPort
import core.outport.DeletePersonPort
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import core.outport.PersistTransactionPort
import core.outport.RequiresTransactionContext
import core.outport.UpdatePersonPort
import core.usecase.AddPersonUsecase
import core.usecase.DeletePersonUsecase
import core.usecase.LoadAllPersonsUsecase
import core.usecase.LoadPersonUsecase
import core.usecase.UpdatePersonUsecase


internal class AddPersonService(
    private val txPort: PersistTransactionPort,
    private val addPersonPort: AddPersonPort,
) : AddPersonUsecase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun addPerson(entry: PersonEntry) = txPort.withNewTransaction {
        addPersonPort.addPerson(entry)
    }
}

internal class LoadPersonService(
    private val txPort: PersistTransactionPort,
    private val loadPersonPort: LoadPersonPort,
) : LoadPersonUsecase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun loadPerson(id: Long) = txPort.withNewTransaction {
        loadPersonPort.loadPerson(id)
    }
}

internal class DeletePersonService(
    private val txPort: PersistTransactionPort,
    private val deletePersonPort: DeletePersonPort,
) : DeletePersonUsecase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun deletePerson(id: Long) = txPort.withNewTransaction {
        deletePersonPort.deletePerson(id)
    }
}

internal class UpdatePersonService(
    private val txPort: PersistTransactionPort,
    private val updatePersonPort: UpdatePersonPort,
) : UpdatePersonUsecase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun updatePerson(entry: PersonEntry) = txPort.withNewTransaction {
        updatePersonPort.updatePerson(entry)
    }
}

internal class LoadAllPersonsService(
    private val txPort: PersistTransactionPort,
    private val loadAllPersonsPort: LoadAllPersonsPort,
) : LoadAllPersonsUsecase {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun loadAllPersons() = txPort.withNewTransaction {
        loadAllPersonsPort.loadAllPersons()
    }
}
