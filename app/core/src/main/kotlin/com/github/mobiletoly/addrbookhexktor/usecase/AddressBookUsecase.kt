package com.github.mobiletoly.addrbookhexktor.usecase

data class AddressBookEntry(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val gender: Gender?,
    val age: Int?,
    val phoneNumber: String,
    val email: String,
    val postalAddress: PostalAddress?
) {
    enum class Gender {
        MALE,
        FEMALE
    }

    data class PostalAddress(
        val address1: String,
        val address2: String?,
        val city: String,
        val state: String,
        val country: String
    ) {
        companion object
    }

    companion object
}

interface AddAddrBookEntryUsecase {
    suspend fun addAddressBookEntry(entry: AddressBookEntry)
}

interface LoadAddressBookEntryUsecase {
    suspend fun loadAddressBookEntryOrNil(id: Long): AddressBookEntry?
}

interface DeleteAddressBookEntryUsecase {
    suspend fun deleteAddressBookEntry(id: Long)
}

interface UpdateAddressBookEntryUsecase {
    suspend fun updateAddressBookEntry(entry: AddressBookEntry): AddressBookEntry
}
