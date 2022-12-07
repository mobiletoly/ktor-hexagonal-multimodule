package core.models

import core.errors.ResourceNotFoundException

data class PersonEntry(
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

class PersonEntryNotFoundException(
    searchCriteria: String
) : ResourceNotFoundException(
    title = "Requested person not found",
    detail = "Person entry not found for search criteria: $searchCriteria",
)
