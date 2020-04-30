package adapters.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature

class HttpClientFactoryImpl : HttpClientFactory {

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
        }
    }

    override fun httpClient() = _httpClient
}
