package com.github.mobiletoly.addrbookhexktor.core.service

import com.github.mobiletoly.addrbookhexktor.usecase.AddAddrBookEntryUsecase
import com.github.mobiletoly.addrbookhexktor.usecase.AddressBookEntry
import com.github.mobiletoly.addrbookhexktor.usecase.DeleteAddressBookEntryUsecase
import com.github.mobiletoly.addrbookhexktor.usecase.LoadAddressBookEntryUsecase
import com.github.mobiletoly.addrbookhexktor.usecase.UpdateAddressBookEntryUsecase


internal class AddressBookService internal constructor(

) : AddAddrBookEntryUsecase,
    LoadAddressBookEntryUsecase,
    DeleteAddressBookEntryUsecase,
    UpdateAddressBookEntryUsecase {

    override suspend fun addAddressBookEntry(entry: AddressBookEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun loadAddressBookEntryOrNil(id: Long): AddressBookEntry? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddressBookEntry(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAddressBookEntry(entry: AddressBookEntry): AddressBookEntry {
        TODO("Not yet implemented")
    }
}
