package adapters.remoting

import io.ktor.client.HttpClient

internal interface HttpClientFactory {
    fun httpClient(): HttpClient
}
