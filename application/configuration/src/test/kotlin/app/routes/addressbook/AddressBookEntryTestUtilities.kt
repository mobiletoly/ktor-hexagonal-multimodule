package app.routes.addressbook

import adapters.persistence.addressbook.AddressBookItemRepository
import adapters.primary.web.routes.addressbook.dto.AddressBookEntryResponseDto
import adapters.primary.web.routes.addressbook.dto.GenderDto
import adapters.primary.web.routes.addressbook.dto.SaveAddressBookEntryRequestDto
import app.util.transactionService
import io.ktor.server.testing.TestApplicationEngine
import org.amshove.kluent.`should be equal to`
import org.koin.ktor.ext.inject
import ports.input.RequiresTransactionContext
import ports.models.AddressBookEntry
import ports.output.addressbook.SaveAddressBookEntryPort

fun buildSaveAddressBookEntryRequestDto1() = SaveAddressBookEntryRequestDto(
    firstName = "Toly",
    lastName = "Pochkin",
    gender = GenderDto.MALE,
    age = 40,
    phoneNumber = "+99-999-123-4567",
    email = "toly.pochkin@someemailprovider.us",
    postalAddress = SaveAddressBookEntryRequestDto.PostalAddressDto(
        address1 = "1234 SW Funny Street",
        address2 = null,
        city = "Seattle",
        state = "WA",
        country = "USA"
    )
)

fun buildSaveAddressBookEntryRequestDto2() = SaveAddressBookEntryRequestDto(
    firstName = "Beth",
    lastName = "Keller",
    gender = GenderDto.FEMALE,
    age = 34,
    phoneNumber = "+99-999-777-1234",
    email = "beth.keller@someemailprovider.us",
    postalAddress = SaveAddressBookEntryRequestDto.PostalAddressDto(
        address1 = "5678 NE Smart Way",
        address2 = null,
        city = "Seattle",
        state = "WA",
        country = "USA"
    )
)

fun buildSaveAddressBookEntryPayloadWithMissingField(): Map<String, Any?> = mapOf(
    "firstName" to "Toly",
    "lastName" to "Pochkin",
    "gender" to GenderDto.MALE,
    "age" to 40,
    "phoneNumber" to "+99-999-123-4567"
    // mandatory email is missing
)

@OptIn(RequiresTransactionContext::class)
suspend fun TestApplicationEngine.addAddressBookEntry1(): AddressBookEntry {
    val addressBookEntryToPost = buildSaveAddressBookEntryRequestDto1()
    val saveAddressBookEntryPort: SaveAddressBookEntryPort by application.inject()
    return transactionService().transaction {
        saveAddressBookEntryPort.addAddressBookEntry(addressBookEntryToPost.toAddressBookEntry(id = null))
    }
}

fun validateRequestDtoMatchesResponseDto(
    requestDto: SaveAddressBookEntryRequestDto,
    responseDto: AddressBookEntryResponseDto
) {
    with(responseDto) {
        firstName `should be equal to` requestDto.firstName
        lastName `should be equal to` requestDto.lastName
        gender `should be equal to` requestDto.gender
        age `should be equal to` requestDto.age
        phoneNumber `should be equal to` requestDto.phoneNumber
        email `should be equal to` requestDto.email
        with(postalAddress) {
            val addressResponse = requestDto.postalAddress
            this?.address1 `should be equal to` addressResponse?.address1
            this?.address2 `should be equal to` addressResponse?.address2
            this?.city `should be equal to` addressResponse?.city
            this?.state `should be equal to` addressResponse?.state
            this?.country `should be equal to` addressResponse?.country
        }
    }
}

@OptIn(RequiresTransactionContext::class)
suspend fun TestApplicationEngine.countAddressBookItems(): Long {
    val addressBookItemRepository by application.inject<AddressBookItemRepository>()
    return transactionService().transaction {
        addressBookItemRepository.count()
    }
}
