package app.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.setBody

/**
 * Make a test request
 */
inline fun <T : Any> TestApplicationEngine.performJsonRequest(
    method: HttpMethod,
    uri: String,
    value: T?,
    response: TestApplicationResponse.() -> Unit = {}
) {
    val call = handleRequest {
        this.uri = uri
        this.method = method
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        value?.let {
            val payload = jacksonObjectMapper().writeValueAsString(value)
            setBody(payload)
        }
    }
    response.invoke(call.response)
}

inline fun TestApplicationEngine.performRequest(
    method: HttpMethod,
    uri: String,
    response: TestApplicationResponse.() -> Unit = {}
) {
    val call = handleRequest {
        this.uri = uri
        this.method = method
        addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
    }
    response.invoke(call.response)
}
