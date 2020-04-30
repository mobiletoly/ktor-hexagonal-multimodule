package domain.addressbook

import ports.provided.addressbook.AddressBookItemResponseDto
import ports.provided.addressbook.AddressBookService
import ports.required.RequiresTransactionContext
import ports.required.TransactionService
import ports.required.addressbook.AddressBookItemRepository
import ports.required.addressbook.PostalAddressRepository
import shared.util.d
import mu.KotlinLogging
import ports.provided.addressbook.AddressBookItemNotFoundException
import ports.provided.addressbook.SaveAddressBookItemRequestDto
import ports.required.addressbook.AddressBookItem
import ports.required.randomperson.RandomPersonClient

private val logger = KotlinLogging.logger { }

class AddressBookServiceImpl(
    private val addressBookItemRepository: AddressBookItemRepository,
    private val postalAddressRepository: PostalAddressRepository,
    private val randomPersonClient: RandomPersonClient,
    private val txService: TransactionService
) : AddressBookService {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun queryAllAddressBookItems() = txService.newTransaction {
        logger.d("queryAllAddressBookItems") { "Query all AddressBookItem records" }
        val addressBookItems = addressBookItemRepository.getAll()
        val postalAddressesMap = postalAddressRepository.getAll()
            .map {
                it.addressBookItemId to it
            }
            .toMap()
        addressBookItems
            .map { addressBookItem ->
                val postalAddress = postalAddressesMap[addressBookItem.id!!]
                addressBookItem.toResponse(postalAddress = postalAddress?.toResponse())
            }
    }

    @OptIn(RequiresTransactionContext::class)
    @Throws(AddressBookItemNotFoundException::class)
    override suspend fun queryAddressBookItem(
        id: Long
    ): AddressBookItemResponseDto = txService.newTransaction {
        logger.d("queryAddressBookItem") { "Query AddressBookItem by id=$id" }
        val addressBookItem = getAddressBookItemById(id = id)
        val postalAddress = postalAddressRepository.getByAddressBookItemIdOrNull(id = addressBookItem.id!!)
        addressBookItem.toResponse(postalAddress = postalAddress?.toResponse())
    }

    @OptIn(RequiresTransactionContext::class)
    @Throws(AddressBookItemNotFoundException::class)
    override suspend fun addAddressBookItem(
        addressBookItemRequest: SaveAddressBookItemRequestDto
    ): AddressBookItemResponseDto = txService.newTransaction {
        logger.d("addAddressBookItem") { "Add AddressBookItem: $addressBookItemRequest" }
        val addressBookItemToSave = addressBookItemRequest.buildAddressBookItem(id = null)
        val addressBookItem = addressBookItemRepository.upsert(entity = addressBookItemToSave)
        val postalAddress = addressBookItemRequest.address
            ?.buildPostalAddress(id = null, addressBookItemId = addressBookItem.id!!)
            ?.let {
                postalAddressRepository.upsert(entity = it)
            }
        addressBookItem.toResponse(postalAddress = postalAddress?.toResponse())
    }

    @OptIn(RequiresTransactionContext::class)
    @Throws(AddressBookItemNotFoundException::class)
    override suspend fun deleteAddressBookItem(
        id: Long
    ) = txService.newTransaction {
        logger.d("deleteAddressBookItem") { "Delete AddressBookItem by id=$id" }
        if (!addressBookItemRepository.deleteById(id = id)) {
            throw AddressBookItemNotFoundException(searchCriteria = "id=$id")
        }
    }

    @OptIn(RequiresTransactionContext::class)
    override suspend fun addRandomAddressBookItem(): AddressBookItemResponseDto {
        logger.d("addRandomAddressBookItem") { "Add single random AddressBookItem record" }
        val randomPerson = randomPersonClient.fetchRandomPerson()
        return txService.newTransaction {
            val addressBookItemToSave = randomPerson.buildAddressBookItem()
            val addressBookItem = addressBookItemRepository.upsert(addressBookItemToSave)
            val postalAddressToSave = randomPerson.buildPostalAddress(addressBookItemId = addressBookItem.id!!)
            val postalAddress = postalAddressRepository.upsert(postalAddressToSave)
            addressBookItem.toResponse(postalAddress = postalAddress.toResponse())
        }
    }

    @OptIn(RequiresTransactionContext::class)
    @Throws(AddressBookItemNotFoundException::class)
    override suspend fun updateAddressBookItem(
        id: Long,
        addressBookItemRequest: SaveAddressBookItemRequestDto
    ): AddressBookItemResponseDto = txService.newTransaction {
        logger.d("updateAddressBookItem") { "Update AddressBookItem by id=$id: $addressBookItemRequest" }
        if (! addressBookItemRepository.hasEntityWithId(id = id)) {
            throw AddressBookItemNotFoundException(searchCriteria = "id=$id")
        }
        val addressBookItemToSave = addressBookItemRequest.buildAddressBookItem(id = id)
        val savedAddressBookItem = addressBookItemRepository.upsert(entity = addressBookItemToSave)
        val address = addressBookItemRequest.address
        val postalAddressResponse = if (address == null) {
            postalAddressRepository.deleteByAddressBookItemId(id = id)
            null
        } else {
            val existingPostalAddress = postalAddressRepository.getByAddressBookItemIdOrNull(id = id)
            val postalAddressToSave = address.buildPostalAddress(
                id = existingPostalAddress?.id,
                addressBookItemId = savedAddressBookItem.id!!
            )
            postalAddressRepository.upsert(entity = postalAddressToSave)
            postalAddressToSave.toResponse()
        }
        savedAddressBookItem.toResponse(postalAddress = postalAddressResponse)
    }

    @RequiresTransactionContext
    private suspend fun getAddressBookItemById(id: Long): AddressBookItem = txService.existingTransaction {
        addressBookItemRepository.getByIdOrNull(id = id)
            ?: throw AddressBookItemNotFoundException(searchCriteria = "id=$id")
    }
}
