package adapters.primaryweb

import adapters.primaryweb.util.RestGenericException
import core.errors.DomainException
import core.errors.ResourceAlreadyExistsException
import core.errors.ResourceNotFoundException
import io.ktor.http.HttpStatusCode

internal fun DomainException.toRestGenericException() = RestGenericException(
    type = errorType,
    title = title,
    status = guessHttpStatusCode(),
    detail = detail,
    specifics = specifics,
)

private fun DomainException.guessHttpStatusCode(): HttpStatusCode = when (this) {
    is ResourceNotFoundException -> HttpStatusCode.NotFound
    is ResourceAlreadyExistsException -> HttpStatusCode.Conflict
    // TODO add your other domain exceptions here
    else -> HttpStatusCode.InternalServerError
}
