package adapters.remoting.randomperson

import adapters.config.AppConfig
import adapters.remoting.HttpClientFactory
import adapters.remoting.randomperson.dto.RandomPersonResponseDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import mu.KotlinLogging
import shared.util.d

private val logger = KotlinLogging.logger { }

internal class RandomPersonHttpClient(
    private val appConfig: AppConfig,
    private val httpClientFactory: HttpClientFactory
) {

    suspend fun fetchRandomPerson(): RandomPersonResponseDto {
        logger.d("fetchRandomPerson") { "Perform HTTP GET request to URL=${appConfig.randomPerson.fetchUrl}" }
        return httpClientFactory.httpClient()
            .get(urlString = appConfig.randomPerson.fetchUrl) {
                parameter("apikey", appConfig.randomPerson.apiKey)
            }
    }
}
