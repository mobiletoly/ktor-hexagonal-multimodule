package adapters.clients.randomperson

import adapters.config.AppConfig
import adapters.http.HttpClientFactory
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import mu.KotlinLogging
import ports.required.randomperson.RandomPerson
import ports.required.randomperson.RandomPersonClient
import shared.util.d

private val logger = KotlinLogging.logger { }

class RandomPersonHttpClient(
    private val appConfig: AppConfig,
    private val httpClientFactory: HttpClientFactory
) : RandomPersonClient {

    override suspend fun fetchRandomPerson(): RandomPerson {
        logger.d("fetchRandomPerson") { "Perform HTTP GET request to URL=${appConfig.randomPerson.fetchUrl}" }
        val response: RandomPersonResponse = httpClientFactory.httpClient()
            .get(urlString = appConfig.randomPerson.fetchUrl) {
                parameter("apikey", appConfig.randomPerson.apiKey)
            }
        return response.toRandomPerson()
    }
}
