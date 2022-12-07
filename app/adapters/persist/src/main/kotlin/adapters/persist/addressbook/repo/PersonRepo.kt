package adapters.persist.addressbook.repo

import adapters.persist.util.postgresql.pgInsertOrUpdate
import com.github.michaelbull.logging.InlineLogger
import core.outport.RequiresTransactionContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

internal class PersonRepo {

    private val logger = InlineLogger()

    @RequiresTransactionContext
    fun getByIdOrNull(id: Long): PersonSqlEntity? {
        return PersonSqlEntities
            .select {
                PersonSqlEntities.id eq id
            }
            .limit(1)
            .map {
                PersonSqlEntity.fromSqlResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    fun upsert(entity: PersonSqlEntity): PersonSqlEntity {
        logger.debug { "upsert(): Update/insert $entity for id=${entity.id}" }
        return PersonSqlEntities
            .pgInsertOrUpdate(PersonSqlEntities.id) {
                entity.toSqlStatement(it)
            }
            .resultedValues!!
            .first()
            .let {
                PersonSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun getAll(): List<PersonSqlEntity> {
        return PersonSqlEntities
            .selectAll()
            .map {
                PersonSqlEntity.fromSqlResultRow(it)
            }
    }

    @RequiresTransactionContext
    fun deleteById(id: Long): Boolean {
        return PersonSqlEntities
            .deleteWhere {
                PersonSqlEntities.id eq id
            } > 0
    }

    @RequiresTransactionContext
    fun count(): Long {
        return PersonSqlEntities
            .selectAll()
            .count()
    }

    @RequiresTransactionContext
    fun hasEntityWithId(id: Long): Boolean {
        return PersonSqlEntities
            .select {
                PersonSqlEntities.id eq id
            }
            .limit(1)
            .count() > 0
    }
}
