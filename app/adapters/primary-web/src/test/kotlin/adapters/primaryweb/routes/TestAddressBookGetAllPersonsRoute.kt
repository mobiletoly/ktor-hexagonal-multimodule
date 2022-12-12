package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.RestErrorResponse
import adapters.primaryweb.gen.models.RestPersonResponse
import adapters.primaryweb.toResponse
import core.usecase.LoadAllPersonsUsecase
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException

class TestAddressBookGetAllPersonsRoute : DescribeSpec() {
    init {
        describe("GET /persons to get all persons") {
            it("success with all persons available") {
                testApplication {
                    val persons = (1..5).map {
                        createPersonEntryWithPostalAddress(id = it.toLong())
                    }
                    application {
                        defaultModules {
                            single {
                                LoadAllPersonsUsecase { persons }
                            }
                        }
                    }
                    val response = client.get("/persons")
                    response shouldHaveStatus HttpStatusCode.OK
                    val entities = Json.decodeFromString<List<RestPersonResponse>>(response.bodyAsText())
                    entities shouldHaveSize 5
                    entities.forEachIndexed { ind, personResp ->
                        personResp.testPersonWithPostalAddress(id = ind.toLong() + 1, expected = persons[ind].toResponse())
                    }
                }
            }

            it("success with empty array when no persons available") {
                testApplication() {
                    application {
                        defaultModules {
                            single {
                                LoadAllPersonsUsecase { emptyList() }
                            }
                        }
                    }
                    val response = client.get("/persons")
                    response shouldHaveStatus HttpStatusCode.OK
                    val entities = Json.decodeFromString<List<RestPersonResponse>>(response.bodyAsText())
                    entities shouldHaveSize 0
                }
            }

            it("fail with HTTP 500 when error occurred during fetching of persons") {
                testApplication() {
                    application {
                        defaultModules {
                            single {
                                LoadAllPersonsUsecase { throw IOException("failed to read database") }
                            }
                        }
                    }
                    val response = client.get("/persons")
                    response shouldHaveStatus HttpStatusCode.InternalServerError
                    val err = Json.decodeFromString<RestErrorResponse>(response.bodyAsText())
                    err.type shouldBe "/errors/internal-server-error"
                    err.instance shouldBe "/persons"
                    err.detail shouldBe "failed to read database"
                }
            }
        }
    }
}
