package adapters.primary.web.routes.addressbook

import adapters.primary.web.util.longParameter
import adapters.primary.web.routes.addressbook.dto.AddressBookEntryListResponseDto
import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import ports.input.addressbook.LoadAddressBookEntryByIdUseCase
import ports.input.addressbook.LoadAddressBookEntryByIdUseCase.LoadAddressBookEntryByIdUseCaseCommand
import ports.input.addressbook.LoadAllAddressBookEntriesUseCase

class LoadAddressBookEntryRoute(application: Application) {

    private val loadAddressBookEntryByIdUseCase by application.inject<LoadAddressBookEntryByIdUseCase>()
    private val loadAllAddressBookEntriesUseCase by application.inject<LoadAllAddressBookEntriesUseCase>()

    init {
        application.routing {
            route("addressBookEntries") {
                // Get address book entry by id
                get("{id}") {
                    val id = call.longParameter("id")
                    val addressBookEntry = loadAddressBookEntryByIdUseCase.loadAddressBookEntryById(
                        LoadAddressBookEntryByIdUseCaseCommand(id = id)
                    )
                    val response = AddressBookEntryResponseDto.fromAddressBookEntry(addressBookEntry)
                    call.respond(HttpStatusCode.OK, response)
                }
                // Get all address book entries
                get {
                    val addressBookEntries = loadAllAddressBookEntriesUseCase.loadAllAddressBookEntries()
                    val response = AddressBookEntryListResponseDto.fromAddressBookEntryList(addressBookEntries)
                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
    }
}
