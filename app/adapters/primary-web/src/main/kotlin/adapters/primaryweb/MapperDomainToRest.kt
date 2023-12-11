package adapters.primaryweb

import adapters.primaryweb.gen.models.RestGender
import adapters.primaryweb.gen.models.RestPersonResponse
import adapters.primaryweb.gen.models.RestPostalAddressResponse
import core.models.PersonEntry

internal fun PersonEntry.toResponse(): RestPersonResponse = with(this) {
    RestPersonResponse(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toResponse(),
        age = age,
        phoneNumber = phoneNumber,
        email = email,
        postalAddress = postalAddress?.let {
            RestPostalAddressResponse(
                address1 = it.address1,
                address2 = it.address2,
                city = it.city,
                state = it.state,
                country = it.country,
            )
        },
    )
}

internal fun PersonEntry.Gender.toResponse(): RestGender = when (this) {
    PersonEntry.Gender.MALE -> RestGender.male
    PersonEntry.Gender.FEMALE -> RestGender.female
}
