package adapters.clients.randomperson

import ports.required.randomperson.RandomPersonResponseDto

internal fun RandomPersonResponse.toRandomPerson() = with(results.first()) {
    RandomPersonResponseDto(
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
