package adapters.persist

import adapters.persist.addressbook.repo.PersonSqlEntities
import adapters.persist.addressbook.repo.PostalAddressSqlEntities
import adapters.persist.util.DatabaseErrorInspector
import com.github.michaelbull.logging.InlineLogger
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import core.outport.BootPersistStoragePort
import core.outport.MustBeCalledInTransactionContext
import core.outport.PersistTransactionPort
import core.outport.ShutdownPersistStoragePort
import java.util.Properties
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import org.jetbrains.exposed.sql.transactions.transactionManager

internal class DatabaseConnector(
    private val databaseConfig: Properties,
    private val errorInspector: DatabaseErrorInspector,
) : BootPersistStoragePort,
    ShutdownPersistStoragePort,
    PersistTransactionPort {

    private val logger = InlineLogger()
    private lateinit var ds: HikariDataSource
    private lateinit var db: Database

    private val tables = arrayOf(
        PersonSqlEntities,
        PostalAddressSqlEntities,
        // add your tables here
    )

    suspend fun deleteAllTables() {
        logger.debug { "Deleting all tables..." }
        withNewTransaction {
            SchemaUtils.drop(*tables)
        }
    }

    override suspend fun <T> withNewTransaction(block: suspend () -> T): T {
        return try {
            newSuspendedTransaction(Dispatchers.IO, db = db) {
                block()
            }
        } catch (e: ExposedSQLException) {
            logger.error(e) { "withNewTransaction(): SQL error while executing new transaction" }
            e.throwAsDomainException(errorInspector)
        }
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withExistingTransaction(block: suspend () -> T): T {
        val tx = db.transactionManager.currentOrNull()
        if (tx == null) {
            throw IllegalStateException("withExistingTransaction(): no current transaction in context")
        } else if (tx.connection.isClosed) {
            throw IllegalStateException("withExistingTransaction(): current transaction is closed")
        }
        return try {
            tx.withSuspendTransaction {
                block()
            }
        } catch (e: ExposedSQLException) {
            logger.error(e) { "withExistingTransaction(): SQl error while executing existing transaction" }
            e.throwAsDomainException(errorInspector)
        }
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        val tx = TransactionManager.currentOrNull()
        return try {
            if (tx == null || tx.connection.isClosed) {
                newSuspendedTransaction(Dispatchers.IO, db = db) {
                    block()
                }
            } else {
                tx.withSuspendTransaction {
                    block()
                }
            }
        } catch (e: ExposedSQLException) {
            logger.error(e) { "transaction(): SQl error while executing transaction" }
            e.throwAsDomainException(errorInspector)
        }
    }

    override suspend fun <T> bootStorage(preInit: suspend () -> T) {
        logger.info { "Initializing database..." }
        ds = HikariDataSource(HikariConfig(databaseConfig))
        db = Database.connect(ds)

        withNewTransaction {
            preInit.invoke()
            SchemaUtils.create(*tables)
        }
    }

    override fun shutdownStorage() {
        if (this::ds.isInitialized) {
            ds.close()
        } else {
            logger.warn { "Request to close data source is ignored - it was not open" }
        }
    }
}
