package adapters.remoting.randomperson

import adapters.remoting.randomperson.dto.RandomPersonResponseDto
import ports.models.AddressBookEntry
import ports.models.AddressBookEntry.PostalAddress

internal fun RandomPersonResponseDto.toAddressBookEntry() = with(results.first()) {
    AddressBookEntry(
        id = null,
        firstName = name.first,
        lastName = name.last,
        gender = gender.toGender(),
        age = dob.age,
        phoneNumber = phone,
        email = email,
        postalAddress = PostalAddress(
            address1 = "${location.street.number} ${location.street.name}",
            address2 = null,
            city = location.city,
            state = location.state,
            country = location.country
        )
    )
}

private fun String.toGender(): AddressBookEntry.Gender {
    return when (toUpperCase()) {
        "MALE" -> AddressBookEntry.Gender.MALE
        "FEMALE" -> AddressBookEntry.Gender.FEMALE
        else -> throw IllegalArgumentException("Unknown gender value=\"$this\"")
    }
}
