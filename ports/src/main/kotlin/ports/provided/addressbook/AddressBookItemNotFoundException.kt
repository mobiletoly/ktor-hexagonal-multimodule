package ports.provided.addressbook

import ports.provided.ResourceNotFoundException

class AddressBookItemNotFoundException(
    val searchCriteria: String
) : ResourceNotFoundException(
    title = "Address book item not found",
    detail = "AddressBookItem not found for search criteria: $searchCriteria"
)
