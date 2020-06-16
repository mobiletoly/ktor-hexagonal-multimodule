package adapters.remoting.randomperson

import adapters.config.AppConfig
import adapters.remoting.HttpClientFactory
import adapters.remoting.randomperson.dto.RandomPersonResponseDto
import com.github.michaelbull.logging.InlineLogger
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import shared.util.d

internal class RandomPersonHttpClient(
    private val appConfig: AppConfig,
    private val httpClientFactory: HttpClientFactory
) {
    private val logger = InlineLogger()

    suspend fun fetchRandomPerson(): RandomPersonResponseDto {
        logger.d("fetchRandomPerson") { "Perform HTTP GET request to URL=${appConfig.randomPerson.fetchUrl}" }
        return httpClientFactory.httpClient()
            .get(urlString = appConfig.randomPerson.fetchUrl) {
                parameter("apikey", appConfig.randomPerson.apiKey)
            }
    }
}
