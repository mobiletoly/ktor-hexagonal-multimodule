package adapters.db.addressbook

import adapters.db.DatabaseConnector
import adapters.db.postgresql.pgInsertOrUpdate
import ports.required.RequiresTransactionContext
import ports.required.addressbook.AddressBookItemRepository
import ports.required.addressbook.AddressBookItem
import shared.util.d
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

private val logger = KotlinLogging.logger { }

class AddressBookItemRepositoryDbImpl(
    private val dbConnector: DatabaseConnector
) : AddressBookItemRepository {

    @RequiresTransactionContext
    override suspend fun getByIdOrNull(id: Long) = dbConnector.existingTransaction {
        AddressBookItems
            .select {
                AddressBookItems.id eq id
            }
            .limit(1)
            .map {
                AddressBookItem.fromResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    override suspend fun upsert(entity: AddressBookItem) = dbConnector.existingTransaction {
        logger.d("upsert") { "Update/insert $entity for id=${entity.id}" }
        AddressBookItems
            .pgInsertOrUpdate(AddressBookItems.id) {
                entity.toStatement(it)
            }
            .resultedValues!!
            .first()
            .let {
                AddressBookItem.fromResultRow(it)
            }
    }

    @RequiresTransactionContext
    override suspend fun getAll() = dbConnector.existingTransaction {
        AddressBookItems
            .selectAll()
            .map {
                val item = AddressBookItem.fromResultRow(it)
                item.id!! to item
            }
            .toMap()
    }

    @RequiresTransactionContext
    override suspend fun deleteById(id: Long) = dbConnector.existingTransaction {
        AddressBookItems
            .deleteWhere {
                AddressBookItems.id eq id
            } > 0
    }

    @RequiresTransactionContext
    override suspend fun count() = dbConnector.existingTransaction {
        AddressBookItems
            .selectAll()
            .count()
    }

    @RequiresTransactionContext
    override suspend fun hasEntityWithId(id: Long) = dbConnector.existingTransaction {
        AddressBookItems
            .select {
                AddressBookItems.id eq id
            }
            .limit(1)
            .count() > 0
    }
}
