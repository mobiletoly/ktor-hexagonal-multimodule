package adapters.clients.randomperson

import ports.required.randomperson.RandomPerson

internal fun RandomPersonResponse.toRandomPerson() = with(results.first()) {
    RandomPerson(
        firstName = name.first,
        lastName = name.last,
        gender = gender,
        age = dob.age,
        phoneNumber = phone,
        email = email,
        address = "${location.street.number} ${location.street.name}",
        city = location.city,
        state = location.state,
        country = location.country
    )
}
