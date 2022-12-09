package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.RestSavePersonRequest
import adapters.primaryweb.toPersonEntry
import adapters.primaryweb.toResponse
import adapters.primaryweb.util.longParameter
import adapters.primaryweb.util.receiveValidated
import com.github.michaelbull.logging.InlineLogger
import core.usecase.AddPersonUsecase
import core.usecase.DeletePersonUsecase
import core.usecase.LoadAllPersonsUsecase
import core.usecase.LoadPersonUsecase
import core.usecase.UpdatePersonUsecase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

private val logger = InlineLogger()

internal fun Routing.personRoute() {
    route("/persons") {
        getPerson()
        getAllPersons()
        addPerson()
        updatePerson()
        deletePerson()
    }
}

private fun Route.getPerson() {
    val loadPersonUsecase by inject<LoadPersonUsecase>()

    get("{id}") {
        val id = call.longParameter("id")
        val personEntry = loadPersonUsecase.loadPerson(id = id)
        val restPersonResp = personEntry.toResponse()
        call.respond(status = HttpStatusCode.OK, message = restPersonResp)
    }
}

private fun Route.getAllPersons() {
    val loadAllPersonsUsecase by inject<LoadAllPersonsUsecase>()

    get {
        val allPersonEntries = loadAllPersonsUsecase.loadAllPersons()
        val restPersonsResp = allPersonEntries.map {
            it.toResponse()
        }
        call.respond(status = HttpStatusCode.OK, message = restPersonsResp)
    }
}

private fun Route.addPerson() {
    val addPersonUsecase by inject<AddPersonUsecase>()

    post {
        val restPersonReq = call.receiveValidated<RestSavePersonRequest>()
        val personToSave = restPersonReq.toPersonEntry(id = null)
        val person = addPersonUsecase.addPerson(entry = personToSave)
        val restPersonResp = person.toResponse()
        call.respond(status = HttpStatusCode.OK, message = restPersonResp)
    }
}

private fun Route.updatePerson() {
    val updatePersonUsecase by inject<UpdatePersonUsecase>()

    put("{id}") {
        val id = call.longParameter("id")
        val restPersonReq = call.receiveValidated<RestSavePersonRequest>()
        val personToSave = restPersonReq.toPersonEntry(id = id)
        val person = updatePersonUsecase.updatePerson(entry = personToSave)
        val restPersonResp = person.toResponse()
        call.respond(status = HttpStatusCode.OK, message = restPersonResp)
    }
}

private fun Route.deletePerson() {
    val deletePersonUsecase by inject<DeletePersonUsecase>()

    delete("{id}") {
        val id = call.longParameter("id")
        deletePersonUsecase.deletePerson(id = id)
        call.respond(status = HttpStatusCode.NoContent, message = "")
    }
}
