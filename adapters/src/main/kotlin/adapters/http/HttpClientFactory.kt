package adapters.http

import io.ktor.client.HttpClient

interface HttpClientFactory {
    fun httpClient(): HttpClient
}
