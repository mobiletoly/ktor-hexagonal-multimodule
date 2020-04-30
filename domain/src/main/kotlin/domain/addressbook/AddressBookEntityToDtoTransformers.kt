package domain.addressbook

import ports.provided.addressbook.AddressBookItemResponseDto
import ports.provided.addressbook.GenderDto
import ports.provided.addressbook.PostalAddressResponseDto
import ports.provided.addressbook.SaveAddressBookItemRequestDto
import ports.provided.addressbook.SavePostalAddressRequestDto
import ports.required.addressbook.AddressBookItem
import ports.required.addressbook.Gender
import ports.required.addressbook.PostalAddress

fun GenderDto.toGender(): Gender {
    return when (this) {
        GenderDto.FEMALE -> Gender.FEMALE
        GenderDto.MALE -> Gender.MALE
    }
}

fun Gender.toGenderDto(): GenderDto {
    return when (this) {
        Gender.FEMALE -> GenderDto.FEMALE
        Gender.MALE -> GenderDto.MALE
    }
}

fun SaveAddressBookItemRequestDto.buildAddressBookItem(
    id: Long?
) = AddressBookItem(
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = gender?.toGender(),
    age = age,
    phoneNumber = phoneNumber,
    email = email
)

fun SavePostalAddressRequestDto.buildPostalAddress(
    id: Long? = null,
    addressBookItemId: Long
) = PostalAddress(
    id = id,
    addressBookItemId = addressBookItemId,
    address1 = address1,
    address2 = address2,
    city = city,
    state = state,
    country = country
)

fun PostalAddress.toResponse() =
    PostalAddressResponseDto(
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        country = country
    )

fun AddressBookItem.toResponse(
    postalAddress: PostalAddressResponseDto?
) = AddressBookItemResponseDto(
    id = id!!,
    firstName = firstName,
    lastName = lastName,
    gender = gender?.toGenderDto(),
    age = age,
    phoneNumber = phoneNumber,
    email = email,
    address = postalAddress
)
