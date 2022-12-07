package adapters.primaryweb

import adapters.primaryweb.gen.models.RestGender
import adapters.primaryweb.gen.models.RestSavePersonRequest
import core.models.PersonEntry

internal fun RestSavePersonRequest.toPersonEntry(id: Long?) = PersonEntry(
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = gender?.toGender(),
    age = age,
    phoneNumber = phoneNumber,
    email = email,
    postalAddress = postalAddress?.let {
        PersonEntry.PostalAddress(
            address1 = it.address1,
            address2 = it.address2,
            city = it.city,
            state = it.state,
            country = it.country
        )
    }
)

private fun RestGender.toGender(): PersonEntry.Gender = when (this) {
    RestGender.male -> PersonEntry.Gender.MALE
    RestGender.female -> PersonEntry.Gender.FEMALE
}
