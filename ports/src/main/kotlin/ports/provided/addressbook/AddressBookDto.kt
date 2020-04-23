package ports.provided.addressbook

enum class GenderDto {
    MALE,
    FEMALE
}

data class SaveAddressBookItemRequestDto(
    val firstName: String,
    val lastName: String,
    val gender: GenderDto? = null,
    val age: Int? = null,
    val phoneNumber: String,
    val email: String,
    val address: SavePostalAddressRequestDto? = null
)

data class SavePostalAddressRequestDto(
    val address1: String,
    val address2: String?,
    val city: String,
    val state: String,
    val country: String
)

data class AddressBookItemResponseDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val gender: GenderDto?,
    val age: Int?,
    val phoneNumber: String,
    val email: String,
    val address: PostalAddressResponseDto?
) {
    companion object
}

data class PostalAddressResponseDto(
    val address1: String,
    val address2: String?,
    val city: String,
    val state: String,
    val country: String
)
