package adapters.persistence.addressbook

import adapters.persistence.util.postgresql.pgInsertOrUpdate
import com.github.michaelbull.logging.InlineLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ports.input.RequiresTransactionContext
import shared.util.d

class PostalAddressRepository {

    private val logger = InlineLogger()

    @RequiresTransactionContext
    fun getById(id: Long): PostalAddressSqlEntity {
        return PostalAddressSqlEntities
            .select {
                PostalAddressSqlEntities.id eq id
            }
            .limit(1)
            .map {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
            .single()
    }

    @RequiresTransactionContext
    fun getByAddressBookItemIdOrNull(id: Long): PostalAddressSqlEntity? {
        return PostalAddressSqlEntities
            .select {
                PostalAddressSqlEntities.addressBookItemId eq id
            }
            .limit(1)
            .map {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    fun upsert(entity: PostalAddressSqlEntity): PostalAddressSqlEntity {
        logger.d("insert") { "Insert $entity" }
        return PostalAddressSqlEntities
            .pgInsertOrUpdate(PostalAddressSqlEntities.id) {
                entity.toSqlStatement(it)
            }
            .resultedValues!!
            .first()
            .let {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun getAll(): List<PostalAddressSqlEntity> {
        return PostalAddressSqlEntities
            .selectAll()
            .map {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
    }
}
