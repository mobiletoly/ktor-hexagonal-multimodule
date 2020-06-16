package adapters.persistence.addressbook

import adapters.persistence.util.postgresql.pgInsertOrUpdate
import com.github.michaelbull.logging.InlineLogger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ports.input.RequiresTransactionContext
import shared.util.d

class AddressBookItemRepository {

    private val logger = InlineLogger()

    @RequiresTransactionContext
    fun getByIdOrNull(id: Long): AddressBookItemSqlEntity? {
        return AddressBookItemSqlEntities
            .select {
                AddressBookItemSqlEntities.id eq id
            }
            .limit(1)
            .map {
                AddressBookItemSqlEntity.fromSqlResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    fun upsert(entity: AddressBookItemSqlEntity): AddressBookItemSqlEntity {
        logger.d("upsert") { "Update/insert $entity for id=${entity.id}" }
        return AddressBookItemSqlEntities
            .pgInsertOrUpdate(AddressBookItemSqlEntities.id) {
                entity.toSqlStatement(it)
            }
            .resultedValues!!
            .first()
            .let {
                AddressBookItemSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun getAll(): List<AddressBookItemSqlEntity> {
        return AddressBookItemSqlEntities
            .selectAll()
            .map {
                AddressBookItemSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun deleteById(id: Long): Boolean {
        return AddressBookItemSqlEntities
            .deleteWhere {
                AddressBookItemSqlEntities.id eq id
            } > 0
    }

    @RequiresTransactionContext
    fun count(): Long {
        return AddressBookItemSqlEntities
            .selectAll()
            .count()
    }

    @RequiresTransactionContext
    fun hasEntityWithId(id: Long): Boolean {
        return AddressBookItemSqlEntities
            .select {
                AddressBookItemSqlEntities.id eq id
            }
            .limit(1)
            .count() > 0
    }
}
