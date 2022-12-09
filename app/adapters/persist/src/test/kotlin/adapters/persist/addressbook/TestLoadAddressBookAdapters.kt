package adapters.persist.addressbook

import adapters.persist.DatabaseConnector
import core.models.PersonEntryNotFoundException
import core.outport.AddPersonPort
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.koin.test.inject

class TestLoadAddressBookAdapters : AddressBookPersistSpec() {

    private val addPersonPort by inject<AddPersonPort>()
    private val loadPersonPort by inject<LoadPersonPort>()
    private val loadAllPersonsPort by inject<LoadAllPersonsPort>()

    init {
        beforeEach {
            bootPort.bootStorage { }
        }
        afterEach {
            (shutdownPort as DatabaseConnector).deleteAllTables()
            shutdownPort.shutdownStorage()
        }

        describe("load person") {
            it("success when retrieve multiple persons") {
                val addedPersons = arrayOf(0, 1, 2).map { i ->
                    txPort.withNewTransaction {
                        val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-${i}")
                            .copy(
                                email = "email${i}@example.com",
                                firstName = "FirstName${i}",
                                lastName = "LastName${i}",
                            )
                        addPersonPort.addPerson(personToAdd)
                    }
                }

                val loadedCount = txPort.withNewTransaction {
                    loadAllPersonsPort.loadAllPersons().count()
                }
                loadedCount shouldBe addedPersons.count()

                addedPersons.forEach { entry ->
                    val id = entry.id!!
                    val loadedPerson = txPort.withNewTransaction {
                        loadPersonPort.loadPerson(id = id)
                    }
                    assertPerson(actual = loadedPerson, expected = entry, expectedId = id)
                }
            }

            it("fail when retrieve person with non-existed id") {
                txPort.withNewTransaction {
                    val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    addPersonPort.addPerson(personToAdd)
                }
                shouldThrow<PersonEntryNotFoundException> {
                    txPort.withNewTransaction {
                        loadPersonPort.loadPerson(id = 123)
                    }
                }
            }
        }

        describe("load all persons") {
            it("success when retrieve all persons") {
                val addedPersons = arrayOf(0, 1, 2).map { i ->
                    txPort.withNewTransaction {
                        val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-${i}")
                            .copy(
                                email = "email${i}@example.com",
                                firstName = "FirstName${i}",
                                lastName = "LastName${i}",
                            )
                        addPersonPort.addPerson(personToAdd)
                    }
                }

                val loadedPersons = txPort.withNewTransaction {
                    loadAllPersonsPort.loadAllPersons()
                }.associateBy { it.id!! }
                loadedPersons.count() shouldBe addedPersons.count()

                addedPersons.forEach { entry ->
                    val id = entry.id!!
                    val loadedPerson = loadedPersons.getValue(id)
                    assertPerson(actual = loadedPerson, expected = entry, expectedId = id)
                }
            }
        }
    }
}
