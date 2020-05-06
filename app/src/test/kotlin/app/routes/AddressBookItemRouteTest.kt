package app.routes

import adapters.db.DatabaseConnector
import adapters.rest.RestDuplicateKeyValueException
import adapters.rest.RestErrorResponse
import adapters.rest.RestMissingRequiredJsonFieldException
import app.AppRouteSpek
import app.util.performJsonRequest
import app.util.performRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should be null`
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.spekframework.spek2.style.specification.describe
import ports.provided.ResourceNotFoundException
import ports.provided.addressbook.AddressBookItemResponseDto
import ports.provided.addressbook.AddressBookService
import ports.provided.addressbook.GenderDto
import ports.provided.addressbook.SaveAddressBookItemRequestDto
import ports.provided.addressbook.SavePostalAddressRequestDto
import ports.required.RequiresTransactionContext
import ports.required.addressbook.AddressBookItemRepository

object AddressBookItemRouteTest : AppRouteSpek({

    describe("HTTP POST /addressBookItem") {
        context("with unique and valid payload") {
            it("adds record and returns HTTP 201 Created with payload") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookItemRequestDto1()
                    performJsonRequest(HttpMethod.Post, "/addressBookItems", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.Created
                        val addressBookItem: AddressBookItemResponseDto = jacksonObjectMapper().readValue(content!!)
                        validateRequestDtoMatchesResponseDto(
                            requestDto = addressBookItemToPost,
                            responseDto = addressBookItem
                        )
                    }
                    application.countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("with payload without address") {
            it("adds record and returns HTTP 201 Created with payload without address") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookItemRequestDto1().copy(address = null)
                    performJsonRequest(HttpMethod.Post, "/addressBookItems", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.Created
                        val addressBookItem: AddressBookItemResponseDto = jacksonObjectMapper().readValue(content!!)
                        addressBookItem.id `should be greater than` 0
                        addressBookItem.address.`should be null`()
                    }
                    application.countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("attempt to add record with non-unique fields") {
            it("returns HTTP 401 Bad Request with error payload") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    val addressBookItemToPost = addressBookService.addAddressBookItem1()
                    performJsonRequest(HttpMethod.Post, "/addressBookItems", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.BadRequest
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.BadRequest.value
                        errorResponse.type `should be equal to` RestDuplicateKeyValueException.ERROR_TYPE
                    }
                    application.countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("attempt to add record with missing mandatory field") {
            it("returns HTTP 401 Bad Request with error payload") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookItemPayloadWithMissingField()
                    performJsonRequest(HttpMethod.Post, "/addressBookItems", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.BadRequest
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.BadRequest.value
                        errorResponse.type `should be equal to` RestMissingRequiredJsonFieldException.ERROR_TYPE
                    }
                    application.countAddressBookItems() `should be equal to` 0
                }
            }
        }
    }

    describe("HTTP PUT /addressBookItem") {
        context("with id of existing record") {
            it("update existing record and returns HTTP 200 OK") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    val originalAddrBookItem = addressBookService.addAddressBookItem1()
                    val recordToUpdate = buildSaveAddressBookItemRequestDto2()
                    performJsonRequest(HttpMethod.Put, "/addressBookItems/${originalAddrBookItem.id}", recordToUpdate) {
                        status() `should be equal to` HttpStatusCode.OK
                        val addressBookItem: AddressBookItemResponseDto = jacksonObjectMapper().readValue(content!!)
                        addressBookItem.id `should be equal to` originalAddrBookItem.id
                        validateRequestDtoMatchesResponseDto(
                            requestDto = recordToUpdate,
                            responseDto = addressBookItem
                        )
                    }
                    application.countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("with record id not in database") {
            it("returns HTTP 404 Not Found with error payload") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    addressBookService.addAddressBookItem1()
                    val recordToUpdate = buildSaveAddressBookItemRequestDto2()
                    performJsonRequest(HttpMethod.Put, "/addressBookItems/99999", recordToUpdate) {
                        status() `should be equal to` HttpStatusCode.NotFound
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.NotFound.value
                        errorResponse.type `should be equal to` ResourceNotFoundException.ERROR_TYPE
                    }
                }
            }
        }
    }

    describe("HTTP GET /addressBookItem/:id") {
        context("with :id matching existing record") {
            it("returns HTTP 200 OK with existing record") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    val existingAddrBookItem = addressBookService.addAddressBookItem1()
                    performRequest(HttpMethod.Get, "/addressBookItems/${existingAddrBookItem.id}") {
                        status() `should be equal to` HttpStatusCode.OK
                        val addressBookItem: AddressBookItemResponseDto = jacksonObjectMapper().readValue(content!!)
                        with(addressBookItem) {
                            id `should be equal to` existingAddrBookItem.id
                            firstName `should be equal to` existingAddrBookItem.firstName
                            lastName `should be equal to` existingAddrBookItem.lastName
                            gender `should be equal to` existingAddrBookItem.gender
                            age `should be equal to` existingAddrBookItem.age
                            phoneNumber `should be equal to` existingAddrBookItem.phoneNumber
                            email `should be equal to` existingAddrBookItem.email
                            with(address!!) {
                                val addressResponse = existingAddrBookItem.address!!
                                address1 `should be equal to` addressResponse.address1
                                address2 `should be equal to` addressResponse.address2
                                city `should be equal to` addressResponse.city
                                state `should be equal to` addressResponse.state
                                country `should be equal to` addressResponse.country
                            }
                        }
                    }
                }
            }
        }
        context("with record id not in database") {
            it("returns HTTP 404 Not Found with error payload") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    addressBookService.addAddressBookItem1()
                    performRequest(HttpMethod.Get, "/addressBookItems/999999") {
                        status() `should be equal to` HttpStatusCode.NotFound
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.NotFound.value
                        errorResponse.type `should be equal to` ResourceNotFoundException.ERROR_TYPE
                    }
                }
            }
        }
    }

    describe("HTTP DELETE /addressBookItem/:id") {
        context("with record id not in database") {
            it("returns HTTP 204 No Content with existing record") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    val existingAddrBookItem = addressBookService.addAddressBookItem1()
                    performRequest(HttpMethod.Delete, "/addressBookItems/${existingAddrBookItem.id}") {
                        status() `should be equal to` HttpStatusCode.NoContent
                    }
                    application.countAddressBookItems() `should be equal to` 0
                }
            }
        }
        context("with record id matching existing record") {
            it("returns HTTP 404 Not Found with error payload") {
                withApp {
                    val addressBookService: AddressBookService by application.inject()
                    addressBookService.addAddressBookItem1()
                    performRequest(HttpMethod.Delete, "/addressBookItems/99999") {
                        status() `should be equal to` HttpStatusCode.NotFound
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.NotFound.value
                        errorResponse.type `should be equal to` ResourceNotFoundException.ERROR_TYPE
                    }
                    application.countAddressBookItems() `should be equal to` 1
                }
            }
        }
    }
})

private fun buildSaveAddressBookItemRequestDto1() = SaveAddressBookItemRequestDto(
    firstName = "Toly",
    lastName = "Pochkin",
    gender = GenderDto.MALE,
    age = 40,
    phoneNumber = "+99-999-123-4567",
    email = "toly.pochkin@someemailprovider.us",
    address = SavePostalAddressRequestDto(
        address1 = "1234 SW Funny Street",
        address2 = null,
        city = "Seattle",
        state = "WA",
        country = "USA"
    )
)

private fun buildSaveAddressBookItemRequestDto2() = SaveAddressBookItemRequestDto(
    firstName = "Beth",
    lastName = "Keller",
    gender = GenderDto.FEMALE,
    age = 34,
    phoneNumber = "+99-999-777-1234",
    email = "beth.keller@someemailprovider.us",
    address = SavePostalAddressRequestDto(
        address1 = "5678 NE Smart Way",
        address2 = null,
        city = "Seattle",
        state = "WA",
        country = "USA"
    )
)

private fun buildSaveAddressBookItemPayloadWithMissingField(): Map<String, Any?> = mapOf(
    "firstName" to "Toly",
    "lastName" to "Pochkin",
    "gender" to GenderDto.MALE,
    "age" to 40,
    "phoneNumber" to "+99-999-123-4567"
    // mandatory email is missing
)

private suspend fun AddressBookService.addAddressBookItem1(): AddressBookItemResponseDto {
    val addressBookItemToPost = buildSaveAddressBookItemRequestDto1()
    return addAddressBookItem(addressBookItemToPost)
}

private fun validateRequestDtoMatchesResponseDto(
    requestDto: SaveAddressBookItemRequestDto,
    responseDto: AddressBookItemResponseDto
) {
    with(responseDto) {
        firstName `should be equal to` requestDto.firstName
        lastName `should be equal to` requestDto.lastName
        gender `should be equal to` requestDto.gender
        age `should be equal to` requestDto.age
        phoneNumber `should be equal to` requestDto.phoneNumber
        email `should be equal to` requestDto.email
        with(address!!) {
            val addressResponse = requestDto.address!!
            address1 `should be equal to` addressResponse.address1
            address2 `should be equal to` addressResponse.address2
            city `should be equal to` addressResponse.city
            state `should be equal to` addressResponse.state
            country `should be equal to` addressResponse.country
        }
    }
}

@OptIn(RequiresTransactionContext::class)
private suspend fun Application.countAddressBookItems(): Int {
    return get<DatabaseConnector>().transaction {
        get<AddressBookItemRepository>().count()
    }
}
