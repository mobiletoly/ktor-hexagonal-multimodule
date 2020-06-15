package adapters.persistence.addressbook

import ports.models.AddressBookEntry
import ports.models.AddressBookEntry.PostalAddress
import ports.models.AddressBookEntry.Gender

internal fun AddressBookEntry.Companion.mapFrom(
    addressBookItemSqlEntity: AddressBookItemSqlEntity,
    postalAddressSqlEntity: PostalAddressSqlEntity?
) = with(addressBookItemSqlEntity) {
    AddressBookEntry(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toEntity(),
        age = age,
        phoneNumber = phoneNumber,
        email = email,
        postalAddress = postalAddressSqlEntity?.toEntity()
    )
}

private fun PostalAddressSqlEntity.toEntity() = PostalAddress(
    address1 = address1,
    address2 = address2,
    city = city,
    state = state,
    country = country
)

private fun GenderSqlType.toEntity(): Gender = when (this) {
    GenderSqlType.MALE -> Gender.MALE
    GenderSqlType.FEMALE -> Gender.FEMALE
}

internal fun AddressBookEntry.toAddressBookItemSqlEntity() = with(this) {
    AddressBookItemSqlEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toSqlEntity(),
        age = age,
        phoneNumber = phoneNumber,
        email = email
    )
}

internal fun PostalAddress.toPostalAddressSqlEntity(
    addressBookId: Long,
    postalAddressId: Long?
): PostalAddressSqlEntity? = with(this) {
    PostalAddressSqlEntity(
        id = postalAddressId,
        addressBookItemId = addressBookId,
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        country = country
    )
}

private fun Gender.toSqlEntity(): GenderSqlType = when (this) {
    Gender.MALE -> GenderSqlType.MALE
    Gender.FEMALE -> GenderSqlType.FEMALE
}
