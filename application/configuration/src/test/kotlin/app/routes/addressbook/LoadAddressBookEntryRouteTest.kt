package app.routes.addressbook

import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import adapters.primary.web.util.RestErrorResponse
import app.AppRouteSpek
import app.util.performRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.style.specification.describe
import ports.output.errors.ResourceNotFoundException

object LoadAddressBookEntryRouteTest : AppRouteSpek({

    describe("HTTP GET /addressBookEntries/:id") {
        context("with :id matching existing record") {
            it("returns HTTP 200 OK with existing record") {
                withApp {
                    val existingAddrBookItem = addAddressBookEntry1()
                    performRequest(HttpMethod.Get, "/addressBookEntries/${existingAddrBookItem.id}") {
                        status() `should be equal to` HttpStatusCode.OK
                        val addressBookItem: AddressBookEntryResponseDto = jacksonObjectMapper().readValue(content!!)
                        with(addressBookItem) {
                            id `should be equal to` existingAddrBookItem.id
                            firstName `should be equal to` existingAddrBookItem.firstName
                            lastName `should be equal to` existingAddrBookItem.lastName
                            gender?.toGender() `should be equal to` existingAddrBookItem.gender
                            age `should be equal to` existingAddrBookItem.age
                            phoneNumber `should be equal to` existingAddrBookItem.phoneNumber
                            email `should be equal to` existingAddrBookItem.email
                            with(postalAddress!!) {
                                val addressResponse = existingAddrBookItem.postalAddress!!
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
                    addAddressBookEntry1()
                    performRequest(HttpMethod.Get, "/addressBookEntries/999999") {
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
