package adapters.primary.web.routes.addressbook

import adapters.primary.web.util.longParameter
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import ports.input.addressbook.DeleteAddressBookEntryUseCase
import ports.input.addressbook.DeleteAddressBookEntryUseCase.DeleteAddressBookEntryUseCaseCommand

class DeleteAddressBookEntryRoute(application: Application) {

    private val deleteAddressBookEntryUseCase by application.inject<DeleteAddressBookEntryUseCase>()

    init {
        application.routing {
            route("addressBookEntries") {
                // Delete existing address book item
                delete("{id}") {
                    val id = call.longParameter("id")
                    deleteAddressBookEntryUseCase.deleteAddressBookEntry(
                        DeleteAddressBookEntryUseCaseCommand(id = id)
                    )
                    call.respond(HttpStatusCode.NoContent, "")
                }
            }
        }
    }
}
