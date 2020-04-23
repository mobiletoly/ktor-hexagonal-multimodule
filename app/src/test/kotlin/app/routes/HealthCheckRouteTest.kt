package app.routes

import adapters.config.AppConfig
import app.AppRouteSpek
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import org.amshove.kluent.`should be equal to`
import org.koin.experimental.property.inject
import org.koin.ktor.ext.inject
import org.spekframework.spek2.style.specification.describe
import ports.provided.healthcheck.HealthCheckResponseDto
import ports.provided.util.DateSupplier

object HealthCheckRouteTest : AppRouteSpek({

    describe("HTTP GET /health") {
        context("when application is healthy") {
            it("returns healthy healthcheck response") {
                withApp {
                    with(handleRequest(HttpMethod.Get, "/health")) {
                        val dateSupplier: DateSupplier by application.inject()
                        val appConfig: AppConfig by application.inject()
                        val status = response.status()
                        status `should be equal to` HttpStatusCode.OK
                        val healthCheckResponse: HealthCheckResponseDto = jacksonObjectMapper().readValue(response.content!!)
                        with(healthCheckResponse) {
                            ready `should be equal to` true
                            appVersion `should be equal to` appConfig.deployment.version
                            appBuildNumber `should be equal to` appConfig.deployment.buildNumber
                            responseTimestamp.time `should be equal to` dateSupplier.currentTimeMillis()
                        }
                    }
                }
            }
        }
    }
})
