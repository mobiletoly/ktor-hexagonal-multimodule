package ports.input

interface TransactionService {
    /** Always starts new transaction. */
    @RequiresTransactionContext
    suspend fun <T> newTransaction(block: suspend () -> T): T

    /** Always requires to run inside already started transaction. */
    @RequiresTransactionContext
    suspend fun <T> existingTransaction(block: suspend () -> T): T

    /** Starts new transaction if it has not been started yet or run in existing transaction if it is available. */
    @RequiresTransactionContext
    suspend fun <T> transaction(block: suspend () -> T): T
}

@RequiresOptIn(message = "This API is required to be called from existing transaction context.")
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
annotation class RequiresTransactionContext

@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.EXPRESSION
)
annotation class StartsNewTransaction
