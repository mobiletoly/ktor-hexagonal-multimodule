package adapters.persist.addressbook

import adapters.persist.addressbook.repo.PersonRepo
import adapters.persist.addressbook.repo.PostalAddressRepo
import com.github.michaelbull.logging.InlineLogger
import core.models.PersonEntry
import core.models.PersonEntryNotFoundException
import core.outport.AddPersonPort
import core.outport.DeletePersonPort
import core.outport.MustBeCalledInTransactionContext
import core.outport.UpdatePersonPort

/**
 * Adapter to perform save/delete operations over address book item and postal address repositories.
 */
internal class SavePersonAdapter(
    private val personRepository: PersonRepo,
    private val postalAddressRepo: PostalAddressRepo,
) : AddPersonPort,
    UpdatePersonPort,
    DeletePersonPort {

    private val logger = InlineLogger()

    @MustBeCalledInTransactionContext
    override fun addPerson(entry: PersonEntry): PersonEntry {
        logger.debug { "addPersonEntry(): Add person entry: $entry" }
        require(entry.id == null) { "entry.id must be null" }
        return upsertPersonEntry(personEntry = entry, postalAddressId = null)
    }

    @MustBeCalledInTransactionContext
    override fun updatePerson(entry: PersonEntry): PersonEntry {
        logger.debug { "updatePersonEntry(): Update person entry: $entry" }
        val personId = requireNotNull(entry.id) { "entity.id must not be null" }
        if (!personRepository.hasEntityWithId(id = personId)) {
            throw PersonEntryNotFoundException(searchCriteria = "id=$personId")
        }
        val postalAddressId = postalAddressRepo
            .getByPersonIdOrNull(personId)
            ?.id
        return upsertPersonEntry(
            personEntry = entry,
            postalAddressId = postalAddressId,
        )
    }

    @MustBeCalledInTransactionContext
    private fun upsertPersonEntry(
        personEntry: PersonEntry,
        postalAddressId: Long?,
    ): PersonEntry {
        val addressBookItemSqlEntity = personRepository.upsert(personEntry.toPersonSqlEntity())
        val postalAddressSqlEntity = personEntry.postalAddress
            ?.toPostalAddressSqlEntity(
                personId = addressBookItemSqlEntity.id!!,
                postalAddressId = postalAddressId,
            )
            ?.let {
                postalAddressRepo.upsert(it)
            }
        return PersonEntry.fromEntity(
            personSqlEntity = addressBookItemSqlEntity,
            postalAddressSqlEntity = postalAddressSqlEntity,
        )
    }

    @MustBeCalledInTransactionContext
    override fun deletePerson(id: Long) {
        logger.debug { "deletePersonEntry(): Delete person entity by id=$id" }
        if (!personRepository.deleteById(id = id)) {
            throw PersonEntryNotFoundException(searchCriteria = "id=$id")
        }
    }
}
