package domain.addressbook

import ports.required.addressbook.AddressBookItem
import ports.required.addressbook.Gender
import ports.required.addressbook.PostalAddress
import ports.required.randomperson.RandomPerson
import java.lang.IllegalArgumentException

fun String.toGender(): Gender {
    return when (this.toLowerCase()) {
        "female" -> Gender.FEMALE
        "male" -> Gender.MALE
        else -> {
            throw IllegalArgumentException("Unknown gender value '$this'")
        }
    }
}

fun RandomPerson.buildAddressBookItem() = AddressBookItem(
    id = null,
    firstName = firstName,
    lastName = lastName,
    gender = gender.toGender(),
    age = age,
    phoneNumber = phoneNumber,
    email = email
)

fun RandomPerson.buildPostalAddress(
    addressBookItemId: Long
) = PostalAddress(
    id = null,
    addressBookItemId = addressBookItemId,
    address1 = address,
    address2 = null,
    city = city,
    state = state,
    country = country
)
