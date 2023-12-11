package adapters.primaryweb.util

import com.github.michaelbull.logging.InlineLogger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond

private val logger = InlineLogger()

internal suspend inline fun <reified T : Any> ApplicationCall.receiveValidated(): T {
    return try {
        receive()
    } catch (e: BadRequestException) {
        logger.error(e) { "ApplicationCall.receiveValidated(): Bad request" }
        throw RestBadInputException(e.message ?: e.toString())
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
        detail = e.message ?: e.toString(),
    )
}

internal suspend fun ApplicationCall.respondRestException(ex: RestGenericException) {
    val errorResponse = ex.toRestErrorResponse(path = request.uri)
    this.respond(status = HttpStatusCode.fromValue(errorResponse.status), message = errorResponse)
}
