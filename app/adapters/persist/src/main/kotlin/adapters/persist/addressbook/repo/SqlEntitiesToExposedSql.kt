package adapters.persist.addressbook.repo

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

internal fun PersonSqlEntity.toSqlStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[PersonSqlEntities.id] = id
    }
    it[PersonSqlEntities.firstName] = firstName
    it[PersonSqlEntities.lastName] = lastName
    it[PersonSqlEntities.gender] = gender
    it[PersonSqlEntities.age] = age
    it[PersonSqlEntities.phoneNumber] = phoneNumber
    it[PersonSqlEntities.email] = email
}

internal fun PersonSqlEntity.Companion.fromSqlResultRow(resultRow: ResultRow) =
    PersonSqlEntity(
        id = resultRow[PersonSqlEntities.id],
        firstName = resultRow[PersonSqlEntities.firstName],
        lastName = resultRow[PersonSqlEntities.lastName],
        gender = resultRow[PersonSqlEntities.gender],
        age = resultRow[PersonSqlEntities.age],
        phoneNumber = resultRow[PersonSqlEntities.phoneNumber],
        email = resultRow[PersonSqlEntities.email]
    )

internal fun PostalAddressSqlEntity.toSqlStatement(statement: InsertStatement<Number>) = statement.let {
    id?.let { id ->
        it[PostalAddressSqlEntities.id] = id
    }
    it[PostalAddressSqlEntities.personId] = personId
    it[PostalAddressSqlEntities.address1] = address1
    it[PostalAddressSqlEntities.address2] = address2
    it[PostalAddressSqlEntities.city] = city
    it[PostalAddressSqlEntities.state] = state
    it[PostalAddressSqlEntities.country] = country
}

internal fun PostalAddressSqlEntity.Companion.fromSqlResultRow(resultRow: ResultRow) =
    PostalAddressSqlEntity(
        id = resultRow[PostalAddressSqlEntities.id],
        personId = resultRow[PostalAddressSqlEntities.personId],
        address1 = resultRow[PostalAddressSqlEntities.address1],
        address2 = resultRow[PostalAddressSqlEntities.address2],
        city = resultRow[PostalAddressSqlEntities.city],
        state = resultRow[PostalAddressSqlEntities.state],
        country = resultRow[PostalAddressSqlEntities.country]
    )
