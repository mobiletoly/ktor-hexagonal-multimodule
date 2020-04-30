package adapters.routes

import adapters.rest.longParameter
import adapters.rest.receiveValidated
import ports.provided.addressbook.AddressBookService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import ports.provided.addressbook.SaveAddressBookItemRequestDto

class AddressBookItemRoute(application: Application) {

    private val addressBookService by application.inject<AddressBookService>()

    init {
        application.routing {
            route("addressBookItems") {
                // Query all address book items
                get {
                    val addressBookItemsResponse = addressBookService.queryAllAddressBookItems()
                    call.respond(HttpStatusCode.OK, addressBookItemsResponse)
                }
                // Query address book item by id
                get("{id}") {
                    val id = call.longParameter("id")
                    val addressBookItemResponse = addressBookService.queryAddressBookItem(id = id)
                    call.respond(HttpStatusCode.OK, addressBookItemResponse)
                }
                // Create new address book item
                post {
                    val addressBookItemRequest = call.receiveValidated<SaveAddressBookItemRequestDto>()
                    val addressBookItemResponse = addressBookService.addAddressBookItem(
                        addressBookItemRequest = addressBookItemRequest
                    )
                    call.response.header(HttpHeaders.Location, "${call.request.uri}/${addressBookItemResponse.id}")
                    call.respond(HttpStatusCode.Created, addressBookItemResponse)
                }
                // Update existing address book item by id
                put("{id}") {
                    val id = call.longParameter("id")
                    val addressBookItemRequest = call.receiveValidated<SaveAddressBookItemRequestDto>()
                    val addressBookItemResponse = addressBookService.updateAddressBookItem(
                        id = id,
                        addressBookItemRequest = addressBookItemRequest
                    )
                    call.respond(HttpStatusCode.OK, addressBookItemResponse)
                }
                // Delete existing address book item by id
                delete("{id}") {
                    val id = call.longParameter("id")
                    addressBookService.deleteAddressBookItem(id = id)
                    call.respond(HttpStatusCode.NoContent, "")
                }
                // Generate new random address book item
                post("random") {
                    val addressBookItemResponse = addressBookService.addRandomAddressBookItem()
                    call.respond(HttpStatusCode.OK, addressBookItemResponse)
                }
            }
        }
    }
}
