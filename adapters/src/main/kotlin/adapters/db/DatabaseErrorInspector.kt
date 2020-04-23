package adapters.db

import org.jetbrains.exposed.exceptions.ExposedSQLException

enum class DatabaseErrorState {
    OTHER,
    DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT
}

interface DatabaseErrorInspector {
    fun errorState(e: ExposedSQLException): DatabaseErrorState
}
