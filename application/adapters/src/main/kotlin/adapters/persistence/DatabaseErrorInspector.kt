package adapters.persistence

import org.jetbrains.exposed.exceptions.ExposedSQLException

internal enum class DatabaseErrorState {
    OTHER,
    DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT
}

internal interface DatabaseErrorInspector {
    fun errorState(e: ExposedSQLException): DatabaseErrorState
}
