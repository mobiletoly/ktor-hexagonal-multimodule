package ports.provided.addressbook

interface AddressBookService {
    suspend fun queryAllAddressBookItems(): List<AddressBookItemResponseDto>
    suspend fun queryAddressBookItem(id: Long): AddressBookItemResponseDto
    suspend fun addAddressBookItem(addressBookItemRequest: SaveAddressBookItemRequestDto): AddressBookItemResponseDto
    suspend fun updateAddressBookItem(id: Long, addressBookItemRequest: SaveAddressBookItemRequestDto): AddressBookItemResponseDto
    suspend fun deleteAddressBookItem(id: Long)
    suspend fun addRandomAddressBookItem(): AddressBookItemResponseDto
}
