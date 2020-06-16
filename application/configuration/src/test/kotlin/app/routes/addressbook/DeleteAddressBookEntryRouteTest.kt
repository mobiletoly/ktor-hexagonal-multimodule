package app.routes.addressbook

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

object DeleteAddressBookEntryRouteTest : AppRouteSpek({

    describe("HTTP DELETE /addressBookEntries/:id") {
        context("with record id not in database") {
            it("returns HTTP 204 No Content with existing record") {
                withApp {
                    val existingAddrBookItem = addAddressBookEntry1()
                    performRequest(HttpMethod.Delete, "/addressBookEntries/${existingAddrBookItem.id}") {
                        status() `should be equal to` HttpStatusCode.NoContent
                    }
                    countAddressBookItems() `should be equal to` 0
                }
            }
        }
        context("with record id matching existing record") {
            it("returns HTTP 404 Not Found with error payload") {
                withApp {
                    addAddressBookEntry1()
                    performRequest(HttpMethod.Delete, "/addressBookEntries/99999") {
                        status() `should be equal to` HttpStatusCode.NotFound
                        val errorResponse: RestErrorResponse = jacksonObjectMapper().readValue(content!!)
                        errorResponse.status `should be equal to` HttpStatusCode.NotFound.value
                        errorResponse.type `should be equal to` ResourceNotFoundException.ERROR_TYPE
                    }
                    countAddressBookItems() `should be equal to` 1
                }
            }
        }
    }
})
