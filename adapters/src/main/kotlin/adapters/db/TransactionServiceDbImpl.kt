package adapters.db

import ports.required.RequiresTransactionContext
import ports.required.TransactionService

class TransactionServiceDbImpl(
    private val dbConnector: DatabaseConnector
) : TransactionService {

    @RequiresTransactionContext
    override suspend fun <T> newTransaction(block: suspend () -> T) = dbConnector.newTransaction {
        block()
    }

    @RequiresTransactionContext
    override suspend fun <T> existingTransaction(block: suspend () -> T) = dbConnector.existingTransaction {
        block()
    }
}
