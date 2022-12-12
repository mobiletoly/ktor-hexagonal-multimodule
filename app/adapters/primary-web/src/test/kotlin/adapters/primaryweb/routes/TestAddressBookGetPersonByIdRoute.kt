package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.RestErrorResponse
import adapters.primaryweb.gen.models.RestPersonResponse
import adapters.primaryweb.toResponse
import core.models.PersonEntryNotFoundException
import core.usecase.LoadPersonUsecase
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

class TestAddressBookGetPersonByIdRoute : DescribeSpec() {
    init {
        describe("GET /persons/{id} to get single person") {
            it("success when person available") {
                testApplication {
                    val person = createPersonEntryWithPostalAddress(id = 1)
                    application {
                        defaultModules {
                            single {
                                LoadPersonUsecase { person }
                            }
                        }
                    }
                    val response = client.get("/persons/1")
                    response shouldHaveStatus HttpStatusCode.OK
                    val entity = Json.decodeFromString<RestPersonResponse>(response.bodyAsText())
                    entity.testPersonWithPostalAddress(id = 1, expected = person.toResponse())
                }
            }

            it("fail with HTTP 404 when person is not found") {
                testApplication {
                    application {
                        defaultModules {
                            single {
                                LoadPersonUsecase {
                                    throw PersonEntryNotFoundException(searchCriteria = "id=1")
                                }
                            }
                        }
                    }
                    val response = client.get("/persons/1")
                    response shouldHaveStatus HttpStatusCode.NotFound
                    val entity = Json.decodeFromString<RestErrorResponse>(response.bodyAsText())
                    entity.type shouldBe "/errors/resource-not-found"
                    entity.instance shouldBe "/persons/1"
                    entity.specifics!! shouldHaveSize 1
                    entity.specifics!![0].key shouldBe "searchCriteria"
                    entity.specifics!![0].value shouldBe "id=1"
                    println(entity)
                }
            }

            it("fail with HTTP 500 when error occurred during fetching of person") {
                testApplication {
                    application {
                        defaultModules {
                            single {
                                LoadPersonUsecase { throw IOException("failed to read database") }
                            }
                        }
                    }
                    val response = client.get("/persons/1")
                    response shouldHaveStatus HttpStatusCode.InternalServerError
                    val err = Json.decodeFromString<RestErrorResponse>(response.bodyAsText())
                    err.type shouldBe "/errors/internal-server-error"
                    err.instance shouldBe "/persons/1"
                    err.detail shouldBe "failed to read database"
                }
            }
        }
    }
}
