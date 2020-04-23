package adapters.db.addressbook

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import ports.required.addressbook.Gender

object AddressBookItems : Table(name = "addressbook_items") {
    val id = long("id").autoIncrement()
    val firstName = text("first_name")
    val lastName = text("last_name")
    val gender = enumerationByName("gender", 20, Gender::class).nullable().index()
    val age = integer("age").nullable().index()
    val phoneNumber = text("phone_number").uniqueIndex()
    val email = text("email").uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "PK_addressbookitem_id")

    init {
        // Constraint to keep a combination of firsName/lastName unique.
        // In real-life database we probably don't want to have this constraint for names
        index(isUnique = true, columns = *arrayOf(firstName, lastName))
    }
}

object PostalAddresses : Table("postal_addresses") {
    val id = long("id").autoIncrement()
    val addressBookItemId = long("addressbookitem_id")
        .references(AddressBookItems.id, onDelete = ReferenceOption.CASCADE)
        .uniqueIndex()
    val address1 = text("address1")
    val address2 = text("address2").nullable()
    val city = text("city")
    val state = text("state")
    val country = text("country")

    override val primaryKey = PrimaryKey(id, name = "PK_postaladdress_id")
}
