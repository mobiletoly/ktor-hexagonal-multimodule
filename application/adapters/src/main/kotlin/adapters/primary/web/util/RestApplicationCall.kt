package adapters.primary.web.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.github.michaelbull.logging.InlineLogger
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import shared.util.w

private val logger = InlineLogger()

internal suspend inline fun <reified T : Any> ApplicationCall.receiveValidated(): T {
    return try {
        receive()
    } catch (e: Exception) {
        handleReceiveWithValidationException(e)
    }
}

internal fun ApplicationCall.stringParameter(name: String) =
    this.parameters[name] ?: throw RestMissingRequestParameterException(paramName = name)

internal fun ApplicationCall.intParameter(name: String): Int {
    return try {
        stringParameter(name).toInt()
    } catch (e: NumberFormatException) {
        raiseInvalidRequestParameterFormatException(name = name, e = e)
    }
}

internal fun ApplicationCall.longParameter(name: String): Long {
    return try {
        stringParameter(name).toLong()
    } catch (e: NumberFormatException) {
        raiseInvalidRequestParameterFormatException(name = name, e = e)
    }
}

private fun raiseInvalidRequestParameterFormatException(name: String, e: Exception): Nothing {
    throw RestInvalidRequestParameterFormatException(
        paramName = name,
        detail = e.message ?: "No details"
    )
}

private fun handleReceiveWithValidationException(e: Exception): Nothing {
    logger.w("handleReceiveWithValidationException", e) {
        "Exception has been raised while deserializing payload"
    }
    when (e) {
        is MissingKotlinParameterException -> {
            throw RestMissingRequiredJsonFieldException(
                fieldName = e.parameter.name ?: "UnknownField",
                fieldType = e.parameter.type.toString()
            )
        }
        is InvalidFormatException -> {
            throw RestInvalidFormatJsonFieldException(
                fieldName = e.path.lastOrNull()?.fieldName ?: "UnknownField",
                fieldType = e.targetType.toString()
            )
        }
        is JsonProcessingException -> {
            throw RestErrorParsingJsonException(
                detail = e.message ?: "No details"
            )
        }
        else -> {
            throw e
        }
    }
}

internal suspend fun ApplicationCall.respondRestException(ex: RestGenericException) {
    val errorResponse = ex.toRestErrorResponse(path = request.uri)
    respond(
        status = HttpStatusCode.fromValue(errorResponse.status),
        message = errorResponse
    )
}
