package adapters.primaryweb.util

import adapters.primaryweb.gen.models.RestErrorResponse
import adapters.primaryweb.gen.models.RestErrorSpecificsResponse
import io.ktor.http.HttpStatusCode

internal open class RestGenericException(
    private val type: String,
    private val title: String,
    val status: HttpStatusCode,
    private val detail: String,
    private val specifics: Map<String, Any?>? = null
) : RuntimeException() {
    fun toRestErrorResponse(path: String) = RestErrorResponse(
        type = type,
        title = title,
        status = status.value,
        detail = detail,
        instance = path,
        specifics = specifics?.map { (key, value) ->
            RestErrorSpecificsResponse(key = key, value = value?.toString())
        }
    )

    override fun toString() = "$this type=\"$type\", title=\"$title\", status=\"$status\", " +
        "detail=\"$detail\", specifics=$specifics"
}

internal class RestBadInputException(message: String) : RestGenericException(
    type = ERROR_TYPE,
    title = "Bad request",
    status = HttpStatusCode.BadRequest,
    detail = message,
    specifics = null,
) {
    companion object {
        const val ERROR_TYPE = "/errors/bad-input"
    }
}

internal data class RestMissingRequestParameterException(
    val paramName: String
) : RestGenericException(
    type = ERROR_TYPE,
    title = "Missing request parameter",
    status = HttpStatusCode.BadRequest,
    detail = "Parameter \"$paramName\" must be provided in request",
    specifics = mapOf(
        "paramName" to paramName
    )
) {
    companion object {
        const val ERROR_TYPE = "/errors/missing-request-parameter"
    }
}

internal data class RestInvalidRequestParameterFormatException(
    val paramName: String,
    val detail: String
) : RestGenericException(
    type = ERROR_TYPE,
    title = "Invalid request parameter format",
    status = HttpStatusCode.BadRequest,
    detail = detail,
    specifics = mapOf(
        "paramName" to paramName
    )
) {
    companion object {
        const val ERROR_TYPE = "/errors/invalid-request-parameter-format"
    }
}

internal data class RestDuplicateKeyValueException(
    val sqlError: String?
) : RestGenericException(
    type = ERROR_TYPE,
    title = "Duplicate key/value in entity",
    status = HttpStatusCode.BadRequest,
    detail = "Cannot store data because some fields are duplicated and conflicting with previously stored entries",
    specifics = mapOf(
        "sqlError" to sqlError
    )
) {
    companion object {
        const val ERROR_TYPE = "/errors/duplicate-key-value"
    }
}

internal data class RestSqlException(
    val sqlError: String?
) : RestGenericException(
    type = ERROR_TYPE,
    title = "Error performing SQL request",
    status = HttpStatusCode.InternalServerError,
    detail = "Something went wrong while executing database SQL request",
    specifics = mapOf(
        "sqlError" to sqlError
    )
) {
    companion object {
        const val ERROR_TYPE = "/errors/sql-error"
    }
}

internal class RestExternalServiceCallException(
    specifics: Map<String, Any?>
) : RestGenericException(
    type = ERROR_TYPE,
    title = "Error performing external service call",
    status = HttpStatusCode.BadGateway,
    detail = "External service call response needs to be inspected for error details",
    specifics = specifics
) {
    companion object {
        const val ERROR_TYPE = "/errors/external-service-call"
    }
}
