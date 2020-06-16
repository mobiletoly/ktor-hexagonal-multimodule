package adapters.persistence.util.postgresql

import adapters.persistence.DatabaseErrorInspector
import adapters.persistence.DatabaseErrorState
import org.jetbrains.exposed.exceptions.ExposedSQLException

/**
 * Error code inspector for PostgreSQL.
 */
internal class PgErrorInspector : DatabaseErrorInspector {
    override fun errorState(e: ExposedSQLException): DatabaseErrorState = when (e.sqlState) {
        "23505" -> DatabaseErrorState.DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT
        else -> DatabaseErrorState.OTHER
    }
}
