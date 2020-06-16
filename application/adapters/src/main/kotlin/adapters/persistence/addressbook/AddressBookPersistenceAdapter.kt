package adapters.persistence.addressbook

import com.github.michaelbull.logging.InlineLogger
import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry
import ports.output.addressbook.AddressBookEntryNotFoundException
import ports.output.addressbook.DeleteAddressBookEntryPort
import ports.output.addressbook.LoadAddressBookEntryPort
import ports.output.addressbook.SaveAddressBookEntryPort
import shared.util.d

/**
 * Adapter to address book item and postal address repositories.
 * It can be accessed from Core module only via ports (e.g. LoadAddressBookItemPort to query data).
 */
internal class AddressBookPersistenceAdapter(
    private val addressBookItemRepository: AddressBookItemRepository,
    private val postalAddressRepository: PostalAddressRepository
) : LoadAddressBookEntryPort,
    SaveAddressBookEntryPort,
    DeleteAddressBookEntryPort {

    private val logger = InlineLogger()

    @RequiresTransactionContext
    override fun loadAddressBookEntry(id: Long): AddressBookEntry {
        val addressBookItemSqlEntity = addressBookItemRepository.getByIdOrNull(id = id)
            ?: throw AddressBookEntryNotFoundException(searchCriteria = "id=$id")
        val postalAddressSqlEntity = postalAddressRepository.getByAddressBookItemIdOrNull(id)
        return AddressBookEntry.mapFrom(
            addressBookItemSqlEntity = addressBookItemSqlEntity,
            postalAddressSqlEntity = postalAddressSqlEntity
        )
    }

    @RequiresTransactionContext
    override fun loadAllAddressBookEntries(): List<AddressBookEntry> {
        val addressBookItemSqlEntities = addressBookItemRepository.getAll()
        val postalAddressesSqlEntitiesMap = postalAddressRepository.getAll()
            .map {
                it.addressBookItemId to it
            }
            .toMap()
        return addressBookItemSqlEntities
            .map { addressBookItemSqlEntity ->
                val postalAddressSqlEntity = postalAddressesSqlEntitiesMap[addressBookItemSqlEntity.id!!]
                AddressBookEntry.mapFrom(
                    addressBookItemSqlEntity = addressBookItemSqlEntity,
                    postalAddressSqlEntity = postalAddressSqlEntity
                )
            }
    }

    @RequiresTransactionContext
    override fun addAddressBookEntry(addressBookEntry: AddressBookEntry): AddressBookEntry {
        logger.d("addAddressBookEntry") {
            "Add AddressBookEntry: $addressBookEntry"
        }
        require(addressBookEntry.id == null) { "addressBookItem.id must be null" }
        return upsertAddressBookEntry(addressBookEntry = addressBookEntry, postalAddressId = null)
    }

    @RequiresTransactionContext
    override fun updateAddressBookEntry(addressBookEntry: AddressBookEntry): AddressBookEntry {
        val addressBookItemId = addressBookEntry.id
        logger.d("updateAddressBookEntry") { "Update AddressBookEntry by id=$addressBookItemId: $addressBookEntry" }
        requireNotNull(addressBookItemId) { "addressBookItem.id must not be null" }
        if (!addressBookItemRepository.hasEntityWithId(id = addressBookItemId)) {
            throw AddressBookEntryNotFoundException(searchCriteria = "id=$addressBookItemId")
        }
        val postalAddressId = postalAddressRepository
            .getByAddressBookItemIdOrNull(addressBookItemId)
            ?.id
        return upsertAddressBookEntry(
            addressBookEntry = addressBookEntry,
            postalAddressId = postalAddressId
        )
    }

    @RequiresTransactionContext
    private fun upsertAddressBookEntry(
        addressBookEntry: AddressBookEntry,
        postalAddressId: Long?
    ): AddressBookEntry {
        val addressBookItemSqlEntity = addressBookItemRepository.upsert(
            addressBookEntry.toAddressBookItemSqlEntity()
        )
        val postalAddressSqlEntity = addressBookEntry.postalAddress
            ?.toPostalAddressSqlEntity(
                addressBookId = addressBookItemSqlEntity.id!!,
                postalAddressId = postalAddressId
            )
            ?.let {
                postalAddressRepository.upsert(it)
            }
        return AddressBookEntry.mapFrom(
            addressBookItemSqlEntity = addressBookItemSqlEntity,
            postalAddressSqlEntity = postalAddressSqlEntity
        )
    }

    @RequiresTransactionContext
    override fun deleteAddressBookEntry(id: Long) {
        logger.d("deleteAddressBookEntry") { "Delete AddressBookEntry by id=$id" }
        if (!addressBookItemRepository.deleteById(id = id)) {
            throw AddressBookEntryNotFoundException(searchCriteria = "id=$id")
        }
    }
}
