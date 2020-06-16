package adapters.primary.web.routes.addressbook.dto

import com.fasterxml.jackson.annotation.JsonProperty
import ports.models.AddressBookEntry
import ports.models.AddressBookEntry.Gender

enum class GenderDto {
    MALE,
    FEMALE;

    fun toGender() = when (this) {
        MALE -> Gender.MALE
        FEMALE -> Gender.FEMALE
    }

    companion object {
        fun fromGender(gender: Gender) = when (gender) {
            Gender.MALE -> GenderDto.MALE
            Gender.FEMALE -> GenderDto.FEMALE
        }
    }
}

data class AddressBookEntryResponseDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val gender: GenderDto?,
    val age: Int?,
    val phoneNumber: String,
    val email: String,
    @JsonProperty("address")
    val postalAddress: PostalAddressResponseDto?
) {
    data class PostalAddressResponseDto(
        val address1: String,
        val address2: String?,
        val city: String,
        val state: String,
        val country: String
    ) {
        companion object
    }

    companion object {
        fun fromAddressBookEntry(addressBookEntry: AddressBookEntry): AddressBookEntryResponseDto {
            requireNotNull(addressBookEntry.id) { "addressBookEntry.id must not be null" }
            return with(addressBookEntry) {
                AddressBookEntryResponseDto(
                    id = id!!,
                    firstName = firstName,
                    lastName = lastName,
                    gender = gender?.let { GenderDto.fromGender(it) },
                    age = age,
                    phoneNumber = phoneNumber,
                    email = email,
                    postalAddress = postalAddress?.let {
                        PostalAddressResponseDto(
                            address1 = it.address1,
                            address2 = it.address2,
                            city = it.city,
                            state = it.state,
                            country = it.country
                        )
                    }
                )
            }
        }
    }
}

data class AddressBookEntryListResponseDto(
    val index: Int,
    val count: Int,
    val total: Int,
    val items: Collection<AddressBookEntryResponseDto>
) {
    companion object {
        fun fromAddressBookEntryList(
            addressBookEntries: Collection<AddressBookEntry>
        ): AddressBookEntryListResponseDto {
            val items = addressBookEntries.map {
                AddressBookEntryResponseDto.fromAddressBookEntry(addressBookEntry = it)
            }
            return AddressBookEntryListResponseDto(
                items = items,
                index = 0,
                count = items.size,
                total = items.size
            )
        }
    }
}
