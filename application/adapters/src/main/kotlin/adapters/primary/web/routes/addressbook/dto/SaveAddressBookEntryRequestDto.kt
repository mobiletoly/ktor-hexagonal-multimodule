package adapters.primary.web.routes.addressbook.dto

import com.fasterxml.jackson.annotation.JsonProperty
import ports.models.AddressBookEntry

data class SaveAddressBookEntryRequestDto(
    val firstName: String,
    val lastName: String,
    val gender: GenderDto?,
    val age: Int?,
    val phoneNumber: String,
    val email: String,
    @JsonProperty("address")
    val postalAddress: PostalAddressDto?
) {
    data class PostalAddressDto(
        val address1: String,
        val address2: String?,
        val city: String,
        val state: String,
        val country: String
    )

    fun toAddressBookEntry(id: Long?) = AddressBookEntry(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toGender(),
        age = age,
        phoneNumber = phoneNumber,
        email = email,
        postalAddress = postalAddress?.let {
            AddressBookEntry.PostalAddress(
                address1 = it.address1,
                address2 = it.address2,
                city = it.city,
                state = it.state,
                country = it.country
            )
        }
    )
}
