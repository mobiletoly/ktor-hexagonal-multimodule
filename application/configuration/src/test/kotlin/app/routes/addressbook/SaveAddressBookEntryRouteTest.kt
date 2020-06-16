package app.routes.addressbook

import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import adapters.primary.web.util.RestErrorResponse
import app.AppRouteSpek
import app.util.performJsonRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.shouldBeNull
import org.spekframework.spek2.style.specification.describe
import ports.output.errors.ResourceNotFoundException

object SaveAddressBookEntryRouteTest : AppRouteSpek({

    describe("HTTP POST /addressBookEntries") {
        context("with unique and valid payload") {
            it("adds record and returns HTTP 201 Created with payload") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookEntryRequestDto1()
                    performJsonRequest(HttpMethod.Post, "/addressBookEntries", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.Created
                        val addressBookItem: AddressBookEntryResponseDto = jacksonObjectMapper().readValue(content!!)
                        validateRequestDtoMatchesResponseDto(
                            requestDto = addressBookItemToPost,
                            responseDto = addressBookItem
                        )
                    }
                    countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("with payload without address") {
            it("adds record and returns HTTP 201 Created with payload without address") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookEntryRequestDto1().copy(postalAddress = null)
                    performJsonRequest(HttpMethod.Post, "/addressBookEntries", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.Created
                        val addressBookItem: AddressBookEntryResponseDto = jacksonObjectMapper().readValue(content!!)
                        addressBookItem.id `should be greater than` 0
                        addressBookItem.postalAddress.shouldBeNull()
                    }
                    countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("attempt to add record with non-unique fields") {
            it("returns HTTP 401 Bad Request with error payload") {
                withApp {
                    addAddressBookEntry1()
                    val addressBookItemToPost = buildSaveAddressBookEntryRequestDto1()
                    performJsonRequest(HttpMethod.Post, "/addressBookEntries", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.BadRequest
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.BadRequest.value
                        errorResponse.type `should be equal to` "/errors/duplicate-key-value"
                    }
                    countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("attempt to add record with missing mandatory field") {
            it("returns HTTP 401 Bad Request with error payload") {
                withApp {
                    val addressBookItemToPost = buildSaveAddressBookEntryPayloadWithMissingField()
                    performJsonRequest(HttpMethod.Post, "/addressBookEntries", addressBookItemToPost) {
                        status() `should be equal to` HttpStatusCode.BadRequest
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.BadRequest.value
                        errorResponse.type `should be equal to` "/errors/missing-required-json-field"
                    }
                    countAddressBookItems() `should be equal to` 0
                }
            }
        }
    }

    describe("HTTP PUT /addressBookEntries") {
        context("with id of existing record") {
            it("update existing record and returns HTTP 200 OK") {
                withApp {
                    val originalAddrBookEntry = addAddressBookEntry1()
                    val recordToUpdate = buildSaveAddressBookEntryRequestDto2()
                    performJsonRequest(
                        HttpMethod.Put,
                        "/addressBookEntries/${originalAddrBookEntry.id}",
                        recordToUpdate
                    ) {
                        status() `should be equal to` HttpStatusCode.OK
                        val addressBookItem: AddressBookEntryResponseDto = jacksonObjectMapper().readValue(content!!)
                        addressBookItem.id `should be equal to` originalAddrBookEntry.id
                        validateRequestDtoMatchesResponseDto(
                            requestDto = recordToUpdate,
                            responseDto = addressBookItem
                        )
                    }
                    countAddressBookItems() `should be equal to` 1
                }
            }
        }
        context("with record id not in database") {
            it("returns HTTP 404 Not Found with error payload") {
                withApp {
                    addAddressBookEntry1()
                    val recordToUpdate = buildSaveAddressBookEntryRequestDto2()
                    performJsonRequest(HttpMethod.Put, "/addressBookEntries/99999", recordToUpdate) {
                        status() `should be equal to` HttpStatusCode.NotFound
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.NotFound.value
                        errorResponse.type `should be equal to` ResourceNotFoundException.ERROR_TYPE
                    }
                }
            }
        }
    }
})
