package ports.required.addressbook

import ports.required.RequiresTransactionContext

interface PostalAddressRepository {
    @RequiresTransactionContext
    suspend fun getById(id: Long): PostalAddress

    @RequiresTransactionContext
    suspend fun getByAddressBookItemIdOrNull(id: Long): PostalAddress?

    @RequiresTransactionContext
    suspend fun upsert(entity: PostalAddress): PostalAddress

    @RequiresTransactionContext
    suspend fun getByIds(ids: Collection<Long>): Map<Long, PostalAddress>

    @RequiresTransactionContext
    suspend fun deleteById(id: Long): Boolean

    @RequiresTransactionContext
    suspend fun deleteByAddressBookItemId(id: Long): Boolean
}
