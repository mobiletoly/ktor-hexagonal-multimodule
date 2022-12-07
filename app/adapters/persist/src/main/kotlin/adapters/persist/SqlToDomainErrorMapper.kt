package adapters.persist

import adapters.persist.util.DatabaseErrorInspector
import adapters.persist.util.DatabaseErrorState
import core.errors.ResourceAlreadyExistsException
import org.jetbrains.exposed.exceptions.ExposedSQLException

internal fun ExposedSQLException.throwAsDomainException(inspector: DatabaseErrorInspector): Nothing {
    when (inspector.errorState(e = this)) {
        DatabaseErrorState.DUPLICATE_KEY_VALUE_VIOLATES_UNIQUE_CONSTRAINT ->
            throw ResourceAlreadyExistsException(
                title = "Resource already exists",
                detail = this.message ?: this.toString()
            )
        else ->
            throw this
    }
}
