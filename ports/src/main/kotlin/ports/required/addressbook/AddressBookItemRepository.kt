package ports.required.addressbook

import ports.required.RequiresTransactionContext

interface AddressBookItemRepository {
    @RequiresTransactionContext
    suspend fun getByIdOrNull(id: Long): AddressBookItem?

    @RequiresTransactionContext
    suspend fun upsert(entity: AddressBookItem): AddressBookItem

    @RequiresTransactionContext
    suspend fun getAll(): Collection<AddressBookItem>

    @RequiresTransactionContext
    suspend fun deleteById(id: Long): Boolean

    @RequiresTransactionContext
    suspend fun count(): Int

    @RequiresTransactionContext
    suspend fun hasEntityWithId(id: Long): Boolean
}
