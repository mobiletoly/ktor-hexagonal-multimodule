package adapters.persist.addressbook

import adapters.persist.addressbook.repo.GenderSqlType
import adapters.persist.addressbook.repo.PersonSqlEntity
import adapters.persist.addressbook.repo.PostalAddressSqlEntity
import core.models.PersonEntry

internal fun PersonEntry.Companion.mapFrom(
    personSqlEntity: PersonSqlEntity,
    postalAddressSqlEntity: PostalAddressSqlEntity?
) = with(personSqlEntity) {
    PersonEntry(
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

private fun PostalAddressSqlEntity.toEntity() = PersonEntry.PostalAddress(
    address1 = address1,
    address2 = address2,
    city = city,
    state = state,
    country = country
)

private fun GenderSqlType.toEntity(): PersonEntry.Gender = when (this) {
    GenderSqlType.MALE -> PersonEntry.Gender.MALE
    GenderSqlType.FEMALE -> PersonEntry.Gender.FEMALE
}

internal fun PersonEntry.toPersonSqlEntity() = with(this) {
    PersonSqlEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender?.toSqlEntity(),
        age = age,
        phoneNumber = phoneNumber,
        email = email
    )
}

internal fun PersonEntry.PostalAddress.toPostalAddressSqlEntity(
    personId: Long,
    postalAddressId: Long?
): PostalAddressSqlEntity = with(this) {
    PostalAddressSqlEntity(
        id = postalAddressId,
        personId = personId,
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        country = country
    )
}

private fun PersonEntry.Gender.toSqlEntity(): GenderSqlType = when (this) {
    PersonEntry.Gender.MALE -> GenderSqlType.MALE
    PersonEntry.Gender.FEMALE -> GenderSqlType.FEMALE
}
