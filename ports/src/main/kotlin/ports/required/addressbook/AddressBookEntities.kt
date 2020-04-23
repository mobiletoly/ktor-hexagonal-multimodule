package ports.required.addressbook

enum class Gender {
    MALE,
    FEMALE
}

data class AddressBookItem(
    // id with null will be passed to create a new record in table, non-nullable id - to update table's record
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val gender: Gender?,
    val age: Int?,
    val phoneNumber: String,
    val email: String
) {
    companion object
}

data class PostalAddress(
    val id: Long? = null,
    val addressBookItemId: Long,
    val address1: String,
    val address2: String?,
    val city: String,
    val state: String,
    val country: String
) {
    companion object
}
