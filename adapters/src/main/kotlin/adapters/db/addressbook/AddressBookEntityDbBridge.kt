package adapters.db.addressbook

import ports.required.addressbook.AddressBookItem
import ports.required.addressbook.PostalAddress
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

fun AddressBookItem.toStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[AddressBookItems.id] = id
    }
    it[AddressBookItems.firstName] = firstName
    it[AddressBookItems.lastName] = lastName
    it[AddressBookItems.gender] = gender
    it[AddressBookItems.age] = age
    it[AddressBookItems.phoneNumber] = phoneNumber
    it[AddressBookItems.email] = email
}

fun AddressBookItem.Companion.fromResultRow(resultRow: ResultRow) =
    AddressBookItem(
        id = resultRow[AddressBookItems.id],
        firstName = resultRow[AddressBookItems.firstName],
        lastName = resultRow[AddressBookItems.lastName],
        gender = resultRow[AddressBookItems.gender],
        age = resultRow[AddressBookItems.age],
        phoneNumber = resultRow[AddressBookItems.phoneNumber],
        email = resultRow[AddressBookItems.email]
    )

fun PostalAddress.toStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[PostalAddresses.id] = id
    }
    it[PostalAddresses.addressBookItemId] = addressBookItemId
    it[PostalAddresses.address1] = address1
    it[PostalAddresses.address2] = address2
    it[PostalAddresses.city] = city
    it[PostalAddresses.state] = state
    it[PostalAddresses.country] = country
}

fun PostalAddress.Companion.fromResultRow(resultRow: ResultRow) =
    PostalAddress(
        id = resultRow[PostalAddresses.id],
        addressBookItemId = resultRow[PostalAddresses.addressBookItemId],
        address1 = resultRow[PostalAddresses.address1],
        address2 = resultRow[PostalAddresses.address2],
        city = resultRow[PostalAddresses.city],
        state = resultRow[PostalAddresses.state],
        country = resultRow[PostalAddresses.country]
    )
