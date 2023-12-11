package adapters.remoting.randomperson

import core.models.PersonEntry

internal fun RandomPersonResponseDto.toAddressBookEntry() =
    with(results.first()) {
        PersonEntry(
            id = null,
            firstName = name.first,
            lastName = name.last,
            gender = gender.toGender(),
            age = dob.age,
            phoneNumber = phone,
            email = email,
            postalAddress = PersonEntry.PostalAddress(
                address1 = "${location.street.number} ${location.street.name}",
                address2 = null,
                city = location.city,
                state = location.state,
                country = location.country,
            ),
        )
    }

private fun String.toGender(): PersonEntry.Gender {
    return when (uppercase()) {
        "MALE" -> PersonEntry.Gender.MALE
        "FEMALE" -> PersonEntry.Gender.FEMALE
        else -> throw IllegalArgumentException("Unknown gender value=\"$this\"")
    }
}
