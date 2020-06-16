package adapters.primary.web.routes.addressbook

import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import ports.input.addressbook.PopulateRandomPersonUseCase

class PopulateRandomPersonEntryRoute(application: Application) {

    private val populateRandomPersonUseCase by application.inject<PopulateRandomPersonUseCase>()

    init {
        application.routing {
            route("addressBookEntries/random") {
                // Create new address book item
                post {
                    val addressBookEntry = populateRandomPersonUseCase.populateRandomPerson()
                    call.response.header(HttpHeaders.Location, "${call.request.uri}/${addressBookEntry.id}")
                    val response = AddressBookEntryResponseDto.fromAddressBookEntry(addressBookEntry)
                    call.respond(HttpStatusCode.Created, response)
                }
            }
        }
    }
}
