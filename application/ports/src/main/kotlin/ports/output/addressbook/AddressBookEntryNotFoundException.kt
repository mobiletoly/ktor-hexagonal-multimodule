package ports.output.addressbook

import ports.output.errors.ResourceNotFoundException

class AddressBookEntryNotFoundException(
    val searchCriteria: String
) : ResourceNotFoundException(
    title = "Address book entry not found",
    detail = "AddressBookEntry not found for search criteria: $searchCriteria"
)
