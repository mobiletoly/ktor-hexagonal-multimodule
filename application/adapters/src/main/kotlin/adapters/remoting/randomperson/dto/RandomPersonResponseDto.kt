package adapters.remoting.randomperson.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class RandomPersonResponseDto(
    val results: List<Result>,
    val info: Info
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Result(
        @JsonProperty("gender")
        val gender: String,
        @JsonProperty("name")
        val name: Name,
        @JsonProperty("location")
        val location: Location,
        @JsonProperty("dob")
        val dob: Dob,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("phone")
        val phone: String
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Name(
            @JsonProperty("first")
            val first: String,
            @JsonProperty("last")
            val last: String
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Dob(
            @JsonProperty("age")
            val age: Int
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Location(
            @JsonProperty("street")
            val street: Street,
            @JsonProperty("city")
            val city: String,
            @JsonProperty("state")
            val state: String,
            @JsonProperty("country")
            val country: String
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Street(
                @JsonProperty("number")
                val number: Int,
                @JsonProperty("name")
                val name: String
            )
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Info(
        val version: String
    )
}
