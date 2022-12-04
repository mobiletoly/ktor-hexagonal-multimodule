package com.github.mobiletoly.addrbookhexktor.adapters.remoting.randomperson

import com.github.michaelbull.logging.InlineLogger
import com.github.mobiletoly.addrbookhexktor.adapters.remoting.newHttpClient
import com.github.mobiletoly.addrbookhexktor.common.log.xRequestId
import com.github.mobiletoly.addrbookhexktor.outport.RandomPersonServiceConfig
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
