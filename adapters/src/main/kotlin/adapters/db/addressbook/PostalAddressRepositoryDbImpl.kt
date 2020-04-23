package adapters.db.addressbook

import adapters.db.DatabaseConnector
import adapters.db.postgresql.pgInsertOrUpdate
import ports.required.RequiresTransactionContext
import ports.required.addressbook.PostalAddress
import ports.required.addressbook.PostalAddressRepository
import shared.util.d
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select

private val logger = KotlinLogging.logger { }

class PostalAddressRepositoryDbImpl(
    private val dbConnector: DatabaseConnector
) : PostalAddressRepository {

    @RequiresTransactionContext
    override suspend fun getById(id: Long) = dbConnector.existingTransaction {
        PostalAddresses
            .select {
                PostalAddresses.id eq id
            }
            .limit(1)
            .map {
                PostalAddress.fromResultRow(it)
            }
            .single()
    }

    @RequiresTransactionContext
    override suspend fun getByAddressBookItemIdOrNull(id: Long) = dbConnector.existingTransaction {
        PostalAddresses
            .select {
                PostalAddresses.addressBookItemId eq id
            }
            .limit(1)
            .map {
                PostalAddress.fromResultRow(it)
            }
            .singleOrNull()
    }

    @RequiresTransactionContext
    override suspend fun upsert(entity: PostalAddress) = dbConnector.existingTransaction {
        logger.d("insert") { "Insert $entity" }
        PostalAddresses
            .pgInsertOrUpdate(PostalAddresses.id) {
                entity.toStatement(it)
            }
            .resultedValues!!
            .first()
            .let {
                PostalAddress.fromResultRow(it)
            }
    }

    @RequiresTransactionContext
    override suspend fun getByIds(ids: Collection<Long>) = dbConnector.existingTransaction {
        PostalAddresses
            .select {
                PostalAddresses.id inList ids
            }
            .map {
                val postalAddress = PostalAddress.fromResultRow(it)
                postalAddress.id!! to postalAddress
            }
            .toMap()
    }

    @RequiresTransactionContext
    override suspend fun deleteById(id: Long) = dbConnector.existingTransaction {
        PostalAddresses
            .deleteWhere {
                PostalAddresses.id eq id
            } > 0
    }

    @RequiresTransactionContext
    override suspend fun deleteByAddressBookItemId(id: Long) = dbConnector.existingTransaction {
        PostalAddresses
            .deleteWhere {
                PostalAddresses.addressBookItemId eq id
            } > 0
    }
}
