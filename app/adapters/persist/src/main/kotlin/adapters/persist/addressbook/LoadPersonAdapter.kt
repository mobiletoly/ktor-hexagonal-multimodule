package adapters.persist.addressbook

import adapters.persist.addressbook.repo.PersonRepo
import adapters.persist.addressbook.repo.PostalAddressRepo
import com.github.michaelbull.logging.InlineLogger
import core.models.PersonEntry
import core.models.PersonEntryNotFoundException
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import core.outport.MustBeCalledInTransactionContext

/**
 * Adapter to perform load operations over address book item and postal address repositories.
 */
internal class LoadPersonAdapter(
    private val personRepo: PersonRepo,
    private val postalAddressRepo: PostalAddressRepo,
) : LoadPersonPort,
    LoadAllPersonsPort {

    private val logger = InlineLogger()

    @MustBeCalledInTransactionContext
    override fun loadPerson(id: Long): PersonEntry {
        logger.debug { "loadPerson(): Load person entry: id=$id" }
        val personSqlEntity = personRepo.getByIdOrNull(id = id)
            ?: throw PersonEntryNotFoundException(searchCriteria = "id=$id")
        val postalAddressSqlEntity = postalAddressRepo.getByPersonIdOrNull(id)
        return PersonEntry.fromEntity(
            personSqlEntity = personSqlEntity,
            postalAddressSqlEntity = postalAddressSqlEntity,
        )
    }

    @MustBeCalledInTransactionContext
    override fun loadAllPersons(): Collection<PersonEntry> {
        logger.debug { "loadAllPersons(): Load all person entries" }
        val personSqlEntities = personRepo.getAll()
        val postalAddressSqlEntitiesMap = postalAddressRepo.getAll().associateBy {
            it.personId
        }
        return personSqlEntities
            .map { personSqlEntity ->
                val postalAddressSqlEntity = postalAddressSqlEntitiesMap[personSqlEntity.id!!]
                PersonEntry.fromEntity(
                    personSqlEntity = personSqlEntity,
                    postalAddressSqlEntity = postalAddressSqlEntity,
                )
            }
    }
}
