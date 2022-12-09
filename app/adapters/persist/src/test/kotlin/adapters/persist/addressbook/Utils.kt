package adapters.persist.addressbook

import adapters.persist.persistenceModule
import core.models.PersonEntry
import core.outport.BootPersistStoragePort
import core.outport.GetDatabaseConfigPort
import core.outport.PersistTransactionPort
import core.outport.ShutdownPersistStoragePort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.util.Properties

fun createPostgreSqlContainer() = PostgreSQLContainer<Nothing>("postgres:14.6-alpine").apply {
    startupAttempts = 1
    exposedPorts = listOf(5432)
    waitingFor(Wait.forListeningPort())
}

fun PostgreSQLContainer<Nothing>.createDatabaseConfigPort(): GetDatabaseConfigPort {
    return object : GetDatabaseConfigPort {
        override val database: Properties
            get() = Properties().also {
                it.setProperty("dataSource.url", jdbcUrl)
                it.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
                it.setProperty("dataSource.user", username)
                it.setProperty("dataSource.password", password)
                it.setProperty("autoCommit", "false")
            }
    }
}

abstract class AddressBookPersistSpec(body: AddressBookPersistSpec.() -> Unit = {}) : DescribeSpec(), KoinTest {

    private val postgresqlContainer = createPostgreSqlContainer()

    private val mockModules by lazy {
        module {
            single {
                postgresqlContainer.createDatabaseConfigPort()
            }
        }
    }

    override fun extensions(): List<KoinExtension> {
        return listOf(KoinExtension(modules = listOf(mockModules, persistenceModule)))
    }

    protected val bootPort by inject<BootPersistStoragePort>()
    protected val shutdownPort by inject<ShutdownPersistStoragePort>()

    protected val txPort by inject<PersistTransactionPort>()

    init {
        postgresqlContainer.start()
        body()
    }
}

fun createDefaultPerson(phoneNumber: String): PersonEntry {
    return PersonEntry(
        id = null,
        firstName = "FirstName",
        lastName = "LastName",
        gender = PersonEntry.Gender.MALE,
        age = 30,
        phoneNumber = phoneNumber,
        email = "email1@example.com",
        postalAddress = PersonEntry.PostalAddress(
            address1 = "Some Street 1",
            address2 = "Apt 1",
            city = "Portland",
            state = "OR",
            country = "USA",
        )
    )
}

fun assertPerson(actual: PersonEntry, expected: PersonEntry, expectedId: Long?) {
    actual.id shouldBe expectedId
    actual.firstName shouldBe expected.firstName
    actual.lastName shouldBe expected.lastName
    actual.gender shouldBe expected.gender
    actual.age shouldBe expected.age
    actual.phoneNumber shouldBe expected.phoneNumber
    actual.email shouldBe expected.email
    val addrToAdd = expected.postalAddress
    if (addrToAdd != null) {
        with(actual.postalAddress!!) {
            this.address1 shouldBe addrToAdd.address1
            this.address2 shouldBe addrToAdd.address2
            this.city shouldBe addrToAdd.city
            this.state shouldBe addrToAdd.state
            this.country shouldBe addrToAdd.country
        }
    }
}
