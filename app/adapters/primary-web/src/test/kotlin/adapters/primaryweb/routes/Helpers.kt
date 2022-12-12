package adapters.primaryweb.routes

import adapters.primaryweb.gen.models.RestPersonResponse
import adapters.primaryweb.webBootstrap
import core.models.PersonEntry
import core.outport.DeploymentConfig
import core.outport.GetDeploymentConfigPort
import io.kotest.matchers.shouldBe
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.logger.Level
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

private val getDeploymentConfigPortImpl = object : GetDeploymentConfigPort {
    override val deployment: DeploymentConfig
        get() = DeploymentConfig(
            env = "test",
            version = "0.0",
            buildNumber = "0",
        )
}

fun Application.defaultModules(moduleDeclaration: ModuleDeclaration = {}) {
    install(Koin) {
        slf4jLogger(level = Level.INFO)
        modules(
            module {
                single<GetDeploymentConfigPort> { getDeploymentConfigPortImpl }
                moduleDeclaration()
            }
        )
    }
    webBootstrap()
}

fun createPersonEntryWithPostalAddress(id: Long) = PersonEntry(
    id = id,
    firstName = "FirstName-$id",
    lastName = "LastName-$id",
    phoneNumber = "503-111-$id",
    email = "email$id@example.com",
    gender = PersonEntry.Gender.FEMALE,
    age = 20 + id.toInt(),
    postalAddress = PersonEntry.PostalAddress(
        address1 = "Cool Street $id",
        address2 = "Apt $id",
        city = "Portland",
        state = "OR",
        country = "USA",
    )
)

fun RestPersonResponse.testPersonWithPostalAddress(id: Long, expected: RestPersonResponse) {
    id shouldBe expected.id
    firstName shouldBe expected.firstName
    lastName shouldBe expected.lastName
    phoneNumber shouldBe expected.phoneNumber
    email shouldBe expected.email
    gender shouldBe expected.gender
    age shouldBe expected.age
    with(postalAddress!!) {
        val expectedAddr = expected.postalAddress!!
        address1 shouldBe expectedAddr.address1
        address2 shouldBe expectedAddr.address2
        city shouldBe expectedAddr.city
        state shouldBe expectedAddr.state
        country shouldBe expectedAddr.country
    }
}
