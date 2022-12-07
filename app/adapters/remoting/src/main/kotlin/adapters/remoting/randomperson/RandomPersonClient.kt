package adapters.remoting.randomperson

import adapters.remoting.newHttpClient
import com.github.michaelbull.logging.InlineLogger
import common.log.xRequestId
import core.outport.RandomPersonServiceConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

internal class RandomPersonHttpClient(
    private val config: RandomPersonServiceConfig,
) {
    private val logger = InlineLogger()
    private val httpClient = newHttpClient()

    suspend fun fetchRandomPerson(): RandomPersonResponseDto {
        logger.debug { "fetchRandomPerson(): Perform HTTP GET request to URL=${config.fetchUrl}" }
        return httpClient.get(urlString = config.fetchUrl) {
            parameter("apikey", config.apiKey)
            header(HttpHeaders.XRequestId, xRequestId())
        }.body()
    }
}
