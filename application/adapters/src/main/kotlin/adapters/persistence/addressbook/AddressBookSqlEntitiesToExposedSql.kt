package adapters.persistence.addressbook

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

internal fun AddressBookItemSqlEntity.toSqlStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[AddressBookItemSqlEntities.id] = id
    }
    it[AddressBookItemSqlEntities.firstName] = firstName
    it[AddressBookItemSqlEntities.lastName] = lastName
    it[AddressBookItemSqlEntities.gender] = gender
    it[AddressBookItemSqlEntities.age] = age
    it[AddressBookItemSqlEntities.phoneNumber] = phoneNumber
    it[AddressBookItemSqlEntities.email] = email
}

internal fun AddressBookItemSqlEntity.Companion.fromSqlResultRow(resultRow: ResultRow) =
    AddressBookItemSqlEntity(
        id = resultRow[AddressBookItemSqlEntities.id],
        firstName = resultRow[AddressBookItemSqlEntities.firstName],
        lastName = resultRow[AddressBookItemSqlEntities.lastName],
        gender = resultRow[AddressBookItemSqlEntities.gender],
        age = resultRow[AddressBookItemSqlEntities.age],
        phoneNumber = resultRow[AddressBookItemSqlEntities.phoneNumber],
        email = resultRow[AddressBookItemSqlEntities.email]
    )

internal fun PostalAddressSqlEntity.toSqlStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[PostalAddressSqlEntities.id] = id
    }
    it[PostalAddressSqlEntities.addressBookItemId] = addressBookItemId
    it[PostalAddressSqlEntities.address1] = address1
    it[PostalAddressSqlEntities.address2] = address2
    it[PostalAddressSqlEntities.city] = city
    it[PostalAddressSqlEntities.state] = state
    it[PostalAddressSqlEntities.country] = country
}

internal fun PostalAddressSqlEntity.Companion.fromSqlResultRow(resultRow: ResultRow) =
    PostalAddressSqlEntity(
        id = resultRow[PostalAddressSqlEntities.id],
        addressBookItemId = resultRow[PostalAddressSqlEntities.addressBookItemId],
        address1 = resultRow[PostalAddressSqlEntities.address1],
        address2 = resultRow[PostalAddressSqlEntities.address2],
        city = resultRow[PostalAddressSqlEntities.city],
        state = resultRow[PostalAddressSqlEntities.state],
        country = resultRow[PostalAddressSqlEntities.country]
    )
