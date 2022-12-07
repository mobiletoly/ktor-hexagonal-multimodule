package adapters.persist.addressbook.repo

import adapters.persist.util.postgresql.pgInsertOrUpdate
import com.github.michaelbull.logging.InlineLogger
import core.outport.RequiresTransactionContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

internal class PostalAddressRepo {

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
    fun getByPersonIdOrNull(id: Long): PostalAddressSqlEntity? {
        return PostalAddressSqlEntities
            .select {
                PostalAddressSqlEntities.personId eq id
            }
            .limit(1)
            .map {
                PostalAddressSqlEntity.fromSqlResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    fun upsert(entity: PostalAddressSqlEntity): PostalAddressSqlEntity {
        logger.debug { "upsert(): Update/insert $entity" }
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
