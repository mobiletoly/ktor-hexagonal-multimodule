package adapters.persistence.util.postgresql

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

/**
 * Upsert (Update/Insert) functionality for PostgreSQL.
 */
internal fun <T : Table> T.pgInsertOrUpdate(
    vararg keyColumns: Column<*>,
    body: T.(InsertStatement<Number>) -> Unit
) = PgInsertOrUpdate<Number>(keyColumns = keyColumns, table = this).apply {
    body(this)
    execute(TransactionManager.current())
}

internal class PgInsertOrUpdate<Key : Any> constructor(
    private val keyColumns: Array<out Column<*>>,
    table: Table,
    isIgnore: Boolean = false
) : InsertStatement<Key>(table = table, isIgnore = isIgnore) {

    override fun prepareSQL(transaction: Transaction): String {
        val updateSetter = super.values.keys.joinToString { "${it.name} = EXCLUDED.${it.name}" }
        val onConflict = "ON CONFLICT (${keyColumns.joinToString { it.name } }) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}
