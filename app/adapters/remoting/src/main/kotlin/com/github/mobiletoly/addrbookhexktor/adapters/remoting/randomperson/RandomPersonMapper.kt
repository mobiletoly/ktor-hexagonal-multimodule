package com.github.mobiletoly.addrbookhexktor.adapters.remoting.randomperson

import com.github.mobiletoly.addrbookhexktor.usecase.AddressBookEntry

internal fun RandomPersonResponseDto.toAddressBookEntry() = with(results.first()) {
    AddressBookEntry(
        id = null,
        firstName = name.first,
        lastName = name.last,
        gender = gender.toGender(),
        age = dob.age,
        phoneNumber = phone,
        email = email,
        postalAddress = AddressBookEntry.PostalAddress(
            address1 = "${location.street.number} ${location.street.name}",
            address2 = null,
            city = location.city,
            state = location.state,
            country = location.country
        )
    )
}

private fun String.toGender(): AddressBookEntry.Gender {
    return when (uppercase()) {
        "MALE" -> AddressBookEntry.Gender.MALE
        "FEMALE" -> AddressBookEntry.Gender.FEMALE
        else -> throw IllegalArgumentException("Unknown gender value=\"$this\"")
    }
}
