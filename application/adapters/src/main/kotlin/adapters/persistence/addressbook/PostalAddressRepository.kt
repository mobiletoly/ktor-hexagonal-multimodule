package adapters.persistence.addressbook

import adapters.persistence.util.postgresql.pgInsertOrUpdate
import ports.input.RequiresTransactionContext
import shared.util.d
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

private val logger = KotlinLogging.logger { }

class PostalAddressRepository {
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
    fun getByIds(ids: Collection<Long>): Map<Long, PostalAddressSqlEntity> {
        return PostalAddressSqlEntities
            .select {
                PostalAddressSqlEntities.id inList ids
            }
            .map {
                val postalAddress = PostalAddressSqlEntity.fromSqlResultRow(it)
                postalAddress.id!! to postalAddress
            }
            .toMap()
    }

    @RequiresTransactionContext
    fun getAll(): List<PostalAddressSqlEntity> {
        return PostalAddressSqlEntities
            .selectAll()
            .map {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun deleteById(id: Long): Boolean {
        return PostalAddressSqlEntities
            .deleteWhere {
                PostalAddressSqlEntities.id eq id
            } > 0
    }

    @RequiresTransactionContext
    fun deleteByAddressBookItemId(id: Long): Boolean {
        return PostalAddressSqlEntities
            .deleteWhere {
                PostalAddressSqlEntities.addressBookItemId eq id
            } > 0
    }

    @RequiresTransactionContext
    fun count(): Long {
        return PostalAddressSqlEntities
            .selectAll()
            .count()
    }
}
