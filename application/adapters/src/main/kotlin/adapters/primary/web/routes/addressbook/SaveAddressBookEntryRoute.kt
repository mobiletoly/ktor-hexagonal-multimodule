package adapters.primary.web.routes.addressbook

import adapters.primary.web.util.longParameter
import adapters.primary.web.util.receiveValidated
import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import adapters.primary.web.routes.addressbook.dto.SaveAddressBookEntryRequestDto
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import ports.input.addressbook.AddAddressBookEntryUseCase
import ports.input.addressbook.AddAddressBookEntryUseCase.AddAddressBookEntryCommand
import ports.input.addressbook.UpdateAddressBookEntryUseCase
import ports.input.addressbook.UpdateAddressBookEntryUseCase.UpdateAddressBookEntryCommand

class SaveAddressBookEntryRoute(application: Application) {

    private val addAddressBookEntryUseCase by application.inject<AddAddressBookEntryUseCase>()
    private val updateAddressBookEntryUseCase by application.inject<UpdateAddressBookEntryUseCase>()

    init {
        application.routing {
            route("addressBookEntries") {
                // Create new address book item
                post {
                    val addressBookItemRequest: SaveAddressBookEntryRequestDto = call.receiveValidated()
                    val addressBookEntryToSave = addressBookItemRequest.toAddressBookEntry(id = null)
                    val addressBookEntry = addAddressBookEntryUseCase.addAddressBookEntry(
                        AddAddressBookEntryCommand(addressBookEntryToSave)
                    )
                    call.response.header(HttpHeaders.Location, "${call.request.uri}/${addressBookEntry.id}")
                    val response = AddressBookEntryResponseDto.fromAddressBookEntry(addressBookEntry)
                    call.respond(HttpStatusCode.Created, response)
                }
                // Update existing address book item
                put("{id}") {
                    val id = call.longParameter("id")
                    val addressBookItemRequest: SaveAddressBookEntryRequestDto = call.receiveValidated()
                    val addressBookEntryToUpdate = addressBookItemRequest.toAddressBookEntry(id = id)
                    val addressBookEntry = updateAddressBookEntryUseCase.updateAddressBookEntry(
                        UpdateAddressBookEntryCommand(addressBookEntryToUpdate)
                    )
                    val response = AddressBookEntryResponseDto.fromAddressBookEntry(addressBookEntry)
                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
    }
}
