package adapters.persist.addressbook

import adapters.persist.DatabaseConnector
import core.errors.ResourceAlreadyExistsException
import core.models.PersonEntry
import core.models.PersonEntryNotFoundException
import core.outport.AddPersonPort
import core.outport.UpdatePersonPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.koin.test.inject

class TestSaveAddressBookAdapters : AddressBookPersistSpec() {

    private val addPersonPort by inject<AddPersonPort>()
    private val updatePersonPort by inject<UpdatePersonPort>()

    init {
        beforeEach {
            bootPort.bootStorage { }
        }
        afterEach {
            (shutdownPort as DatabaseConnector).deleteAllTables()
            shutdownPort.shutdownStorage()
        }

        describe("add single person") {
            it("success when person added to empty table") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                val addedPerson = txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                validateSavedPerson(savedPerson = addedPerson, personToSave = personToAdd, expectedId = 1)
            }
            it("success when person added to non-empty table") {
                val initialPersonToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(initialPersonToAdd)
                }
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-5555")
                    .copy(email = "email2@gmail.com", lastName = "SomeOtherLastName")
                val addedPerson = txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                validateSavedPerson(savedPerson = addedPerson, personToSave = personToAdd, expectedId = 2)
            }
            it("fail when person with the same phone already exist") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                val personToAdd2 = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                val e = shouldThrow<ResourceAlreadyExistsException> {
                    txPort.withNewTransaction {
                        addPersonPort.addPerson(personToAdd2)
                    }
                }
                e.detail shouldContain "\"person_phone_number_unique\""
            }
            it("fail when person with the same email already exist") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                val personToAdd2 = createDefaultPerson(phoneNumber = "+1-503-333-5555")
                val e = shouldThrow<ResourceAlreadyExistsException> {
                    txPort.withNewTransaction {
                        addPersonPort.addPerson(personToAdd2)
                    }
                }
                e.detail shouldContain "\"person_email_unique\""
            }
            it("fail when person with the same first and last name already exist") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                val personToAdd2 = createDefaultPerson(phoneNumber = "+1-503-333-5555")
                    .copy(email = "email2@example.com")
                val e = shouldThrow<ResourceAlreadyExistsException> {
                    txPort.withNewTransaction {
                        addPersonPort.addPerson(personToAdd2)
                    }
                }
                e.detail shouldContain "\"person_first_name_last_name_unique\""
            }
            it("fail when attempt to add with id specified in payload") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    .copy(id = 123)
                val e = shouldThrow<IllegalArgumentException> {
                    txPort.withNewTransaction {
                        addPersonPort.addPerson(personToAdd)
                    }
                }
                e.message shouldBe "entry.id must be null"
            }
        }

        describe("update single person") {
            it("success when update existing person") {
                val addedPerson = txPort.withNewTransaction {
                    val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    addPersonPort.addPerson(personToAdd)
                }
                val personToUpdate = addedPerson.copy(
                    firstName = "NewFirstName",
                    lastName = "NewLastName",
                )
                val updatedPerson = txPort.withNewTransaction {
                    updatePersonPort.updatePerson(personToUpdate)
                }
                validateSavedPerson(savedPerson = updatedPerson, personToSave = personToUpdate, expectedId = personToUpdate.id)
            }
            it("fail when update person with non-existing id") {
                val addedPerson = txPort.withNewTransaction {
                    val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                    addPersonPort.addPerson(personToAdd)
                }
                val personToUpdate = addedPerson.copy(
                    id = 123,
                    firstName = "NewFirstName",
                    lastName = "NewLastName",
                )
                shouldThrow<PersonEntryNotFoundException> {
                    txPort.withNewTransaction {
                        updatePersonPort.updatePerson(personToUpdate)
                    }
                }
            }
            it("fail when attempt to update with id not specified in payload") {
                val personToUpdate = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                val e = shouldThrow<IllegalArgumentException> {
                    txPort.withNewTransaction {
                        updatePersonPort.updatePerson(personToUpdate)
                    }
                }
                e.message shouldBe "entity.id must not be null"
            }
            it("fail when attempt to update person with phone number already existing in another person") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                val personToAdd2 = createDefaultPerson(phoneNumber = "+1-503-333-5555").copy(
                    firstName = "NewFirstName",
                    lastName = "NewLastName",
                    email = "email2@gmail.com",
                )
                val updatedPerson2 = txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd2)
                }
                val personToUpdate = updatedPerson2.copy(
                    phoneNumber = personToAdd.phoneNumber,
                )
                val e = shouldThrow<ResourceAlreadyExistsException> {
                    txPort.withNewTransaction {
                        updatePersonPort.updatePerson(personToUpdate)
                    }
                }
                e.detail shouldContain "\"person_phone_number_unique\""
            }
            it("fail when attempt to update person with email already existing in another person") {
                val personToAdd = createDefaultPerson(phoneNumber = "+1-503-333-4444")
                txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd)
                }
                val personToAdd2 = createDefaultPerson(phoneNumber = "+1-503-333-5555").copy(
                    firstName = "NewFirstName",
                    lastName = "NewLastName",
                    email = "email2@gmail.com"
                )
                val updatedPerson2 = txPort.withNewTransaction {
                    addPersonPort.addPerson(personToAdd2)
                }
                val personToUpdate = updatedPerson2.copy(
                    email = personToAdd.email,
                )
                val e = shouldThrow<ResourceAlreadyExistsException> {
                    txPort.withNewTransaction {
                        updatePersonPort.updatePerson(personToUpdate)
                    }
                }
                e.detail shouldContain "\"person_email_unique\""
            }
        }
    }
}

private fun createDefaultPerson(phoneNumber: String): PersonEntry {
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

private fun validateSavedPerson(savedPerson: PersonEntry, personToSave: PersonEntry, expectedId: Long?) {
    savedPerson.id shouldBe expectedId
    savedPerson.firstName shouldBe personToSave.firstName
    savedPerson.lastName shouldBe personToSave.lastName
    savedPerson.gender shouldBe personToSave.gender
    savedPerson.age shouldBe personToSave.age
    savedPerson.phoneNumber shouldBe personToSave.phoneNumber
    savedPerson.email shouldBe personToSave.email
    val addrToAdd = personToSave.postalAddress
    if (addrToAdd != null) {
        with(savedPerson.postalAddress!!) {
            this.address1 shouldBe addrToAdd.address1
            this.address2 shouldBe addrToAdd.address2
            this.city shouldBe addrToAdd.city
            this.state shouldBe addrToAdd.state
            this.country shouldBe addrToAdd.country
        }
    }
}
