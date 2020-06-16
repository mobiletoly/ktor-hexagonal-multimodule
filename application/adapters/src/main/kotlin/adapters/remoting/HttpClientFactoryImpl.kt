package adapters.remoting

import adapters.primary.web.util.RestExternalServiceCallException
import adapters.util.sharedJsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.michaelbull.logging.InlineLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ResponseException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import shared.util.e

internal class HttpClientFactoryImpl : HttpClientFactory {

    private val logger = InlineLogger()

    private val _httpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode in 300..399) {
                        throw RedirectResponseException(response)
                    } else if (statusCode >= 400) {
                        response.throwException()
                    }
                }
            }
        }
    }

    override fun httpClient() = _httpClient

    /**
     * Map HTTP response status to RestExternalServiceCallException with response payload (if possible).
     */
    private suspend fun HttpResponse.throwException(): Nothing {
        val errorMap = try {
            // At first we will try to exract response payload and map it to JSON structure of plain text
            val body = readBytes()
            if (contentType()?.contentType?.contains(ContentType.Application.Json.contentType) == true) {
                sharedJsonMapper.readValue<Map<String, Any>>(body)
            } else {
                mapOf("responseBody" to String(body))
            }
        } catch (e: Throwable) {
            logger.e("HttpResponse.throwException()") { "Failed to map error response" }
            // If previous attempt of mapping failed - we fallback to default behavior
            when (status.value) {
                in 300..399 -> throw RedirectResponseException(this)
                in 400..499 -> throw ClientRequestException(this)
                in 500..599 -> throw ServerResponseException(this)
            }
            throw ResponseException(this)
        }
        throw RestExternalServiceCallException(
            status = status,
            specifics = errorMap
        )
    }
}
