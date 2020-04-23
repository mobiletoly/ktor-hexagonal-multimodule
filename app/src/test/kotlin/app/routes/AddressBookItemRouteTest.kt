package app.routes

import app.AppRouteSpek
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.style.specification.describe
import ports.provided.addressbook.AddressBookItemResponseDto
import ports.provided.addressbook.GenderDto
import ports.provided.addressbook.SaveAddressBookItemRequestDto
import ports.provided.addressbook.SavePostalAddressRequestDto

object AddressBookItemRouteTest : AppRouteSpek({

    describe("HTTP POST /addressBookItem") {
        context("with unique and valid payload") {
            it("returns 201 Created with payload") {
                withApp {
                    val addressBookItemToPost = createSaveAddressBookItemRequestDto()
                    with(
                        handleRequest(HttpMethod.Post, "/addressBookItems") {
                            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            val payload = jacksonObjectMapper().writeValueAsString(addressBookItemToPost)
                            setBody(payload)
                        }
                    ) {
                        val status = response.status()
                        status `should be equal to` HttpStatusCode.Created
                        val addressBookItem: AddressBookItemResponseDto = jacksonObjectMapper().readValue(response.content!!)
                        with(addressBookItem) {
                            firstName `should be equal to` addressBookItemToPost.firstName
                            lastName `should be equal to` addressBookItemToPost.lastName
                            gender `should be equal to` addressBookItemToPost.gender
                            age `should be equal to` addressBookItemToPost.age
                            phoneNumber `should be equal to` addressBookItemToPost.phoneNumber
                            email `should be equal to` addressBookItemToPost.email
                            with(address!!) {
                                val addressResponse = addressBookItem.address!!
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
    }

    describe("HTTP POST /addressBookItem") {
        context("attempt to add record with non-unique fields") {
            it("returns 201 Created with payload") {
                withApp {
                    val addressBookItemToPost = createSaveAddressBookItemRequestDto()
                    with(
                        handleRequest(HttpMethod.Post, "/addressBookItems") {
                            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            val payload = jacksonObjectMapper().writeValueAsString(addressBookItemToPost)
                            setBody(payload)
                        }
                    ) {
                        val status = response.status()
                        status `should be equal to` HttpStatusCode.Created
                    }
                }
            }
        }
    }
})

private fun createSaveAddressBookItemRequestDto() = SaveAddressBookItemRequestDto(
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
