package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.Gender
import adapters.primaryweb.gen.models.PersonResponse
import adapters.primaryweb.gen.models.PostalAddressResponse
import adapters.primaryweb.util.longParameter
import com.github.michaelbull.logging.InlineLogger
import core.models.PersonEntry
import core.usecase.AddPersonUsecase
import core.usecase.LoadPersonUsecase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

private val logger = InlineLogger()

internal fun Routing.personRoute() {
    route("/persons") {
        getPerson()
        addPerson()
    }
}

private fun Route.getPerson() {
    val loadPersonUsecase by inject<LoadPersonUsecase>()

    get("{id}") {
        val id = call.longParameter("id")
        val personEntry = loadPersonUsecase.loadPerson(id = id)
        val response = personEntry.toResponse()
        call.respond(status = HttpStatusCode.OK, message = response)
    }
}

private fun Route.addPerson() {
    val addPersonUsecase by inject<AddPersonUsecase>()

    post {
        val person = PersonEntry(
            firstName = "Toly",
            lastName = "Pochkin",
            gender = PersonEntry.Gender.MALE,
            age = 43,
            phoneNumber = "+1-503-999-9999",
            email = "email@example.com",
            postalAddress = PersonEntry.PostalAddress(
                address1 = "111 Some Street",
                address2 = "Unit 999",
                city = "Portland",
                state = "OR",
                country = "USA",
            ),
        )
        try {
            addPersonUsecase.addPerson(entry = person)
        } catch (e: Exception) {
            logger.warn(e) { "<-------------------------------->" }
            throw e
        }
    }
}

private fun PersonEntry.toResponse(): PersonResponse = with(this) {
    PersonResponse(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toResponse(),
        age = age,
        phoneNumber = phoneNumber,
        email = email,
        postalAddress = postalAddress?.let {
            PostalAddressResponse(
                address1 = it.address1,
                address2 = it.address2,
                city = it.city,
                state = it.state,
                country = it.country
            )
        }
    )
}

private fun PersonEntry.Gender.toResponse(): Gender = when (this) {
    PersonEntry.Gender.MALE -> Gender.male
    PersonEntry.Gender.FEMALE -> Gender.female
}
