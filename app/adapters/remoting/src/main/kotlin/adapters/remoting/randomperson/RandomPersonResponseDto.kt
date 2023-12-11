package adapters.remoting.randomperson

import kotlinx.serialization.Serializable

@Serializable
internal data class RandomPersonResponseDto(
    val results: List<Result>,
    val info: Info,
) {
    @Serializable
    data class Result(
        val gender: String,
        val name: Name,
        val location: Location,
        val dob: Dob,
        val email: String,
        val phone: String,
    ) {
        @Serializable
        data class Name(
            val first: String,
            val last: String,
        )

        @Serializable
        data class Dob(
            val age: Int,
        )

        @Serializable
        data class Location(
            val street: Street,
            val city: String,
            val state: String,
            val country: String,
        ) {
            @Serializable
            data class Street(
                val number: Int,
                val name: String,
            )
        }
    }

    @Serializable
    data class Info(
        val version: String,
    )
}
