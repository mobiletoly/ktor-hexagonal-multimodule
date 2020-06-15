package ports.input.addressbook

interface DeleteAddressBookEntryUseCase {
    suspend fun deleteAddressBookEntry(command: DeleteAddressBookEntryUseCaseCommand)

    data class DeleteAddressBookEntryUseCaseCommand(
        val id: Long
    )
}
