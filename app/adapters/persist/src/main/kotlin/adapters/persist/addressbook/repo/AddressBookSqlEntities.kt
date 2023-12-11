package adapters.persist.addressbook.repo

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// --- TYPES

internal enum class GenderSqlType {
    MALE,
    FEMALE,
}

// --- TABLES

internal object PersonSqlEntities : Table(name = "person") {
    val id = long("id").autoIncrement()
    val firstName = text("first_name")
    val lastName = text("last_name")
    val gender = enumerationByName("gender", 20, GenderSqlType::class).nullable().index()
    val age = integer("age").nullable().index()
    val phoneNumber = text("phone_number").uniqueIndex()
    val email = text("email").uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "PK_person_id")

    init {
        // Constraint to keep a combination of firsName/lastName unique.
        // In real-life database we probably don't want to have this constraint for names
        index(isUnique = true, columns = arrayOf(firstName, lastName))
    }
}

internal object PostalAddressSqlEntities : Table("postal_addresses") {
    val id = long("id").autoIncrement()
    val personId = long("person_id")
        .references(PersonSqlEntities.id, onDelete = ReferenceOption.CASCADE)
        .uniqueIndex()
    val address1 = text("address1")
    val address2 = text("address2").nullable()
    val city = text("city")
    val state = text("state")
    val country = text("country")

    override val primaryKey = PrimaryKey(id, name = "PK_postaladdress_id")
}

// --- ENTITIES

internal data class PersonSqlEntity(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val gender: GenderSqlType?,
    val age: Int?,
    val phoneNumber: String,
    val email: String,
) {
    companion object
}

internal data class PostalAddressSqlEntity(
    val id: Long? = null,
    val personId: Long,
    val address1: String,
    val address2: String?,
    val city: String,
    val state: String,
    val country: String,
) {
    companion object
}
