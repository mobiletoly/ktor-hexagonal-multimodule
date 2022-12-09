package adapters.persist.addressbook

import adapters.persist.DatabaseConnector
import core.models.PersonEntryNotFoundException
import core.outport.AddPersonPort
import core.outport.DeletePersonPort
import core.outport.LoadAllPersonsPort
import core.outport.LoadPersonPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.koin.test.inject

class TestDeleteAddressBookAdapters : AddressBookPersistSpec() {

    private val addPersonPort by inject<AddPersonPort>()
    private val loadPersonPort by inject<LoadPersonPort>()
    private val loadAllPersonsPort by inject<LoadAllPersonsPort>()
    private val deletePersonPort by inject<DeletePersonPort>()

    init {
        beforeEach {
            bootPort.bootStorage { }
        }
        afterEach {
            (shutdownPort as DatabaseConnector).deleteAllTables()
            shutdownPort.shutdownStorage()
        }

        describe("delete person") {
            it("success when delete person") {
                val addedPerson = txPort.withNewTransaction {
                    val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    addPersonPort.addPerson(personToAdd)
                }

                txPort.withNewTransaction {
                    deletePersonPort.deletePerson(id = addedPerson.id!!)
                }

                shouldThrow<PersonEntryNotFoundException> {
                    txPort.withNewTransaction {
                        loadPersonPort.loadPerson(id = addedPerson.id!!)
                    }
                }
            }

            it("success when delete 2 out of 3 persons") {
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

                txPort.withNewTransaction {
                    deletePersonPort.deletePerson(id = addedPersons.first().id!!)
                    deletePersonPort.deletePerson(id = addedPersons.last().id!!)
                }

                val loadedPersons = txPort.withNewTransaction {
                    loadAllPersonsPort.loadAllPersons()
                }
                loadedPersons.count() shouldBe 1
                val loadedPerson = loadedPersons.first()

                assertPerson(actual = loadedPerson, expected = addedPersons[1], expectedId = addedPersons[1].id!!)
            }

            it("fail when delete person with non-existed id") {
                val addedPerson = txPort.withNewTransaction {
                    val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    addPersonPort.addPerson(personToAdd)
                }

                txPort.withNewTransaction {
                    deletePersonPort.deletePerson(id = addedPerson.id!!)
                }

                shouldThrow<PersonEntryNotFoundException> {
                    txPort.withNewTransaction {
                        deletePersonPort.deletePerson(id = addedPerson.id!!)
                    }
                }
            }
        }
    }
}
