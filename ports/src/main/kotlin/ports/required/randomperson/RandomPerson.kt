package ports.required.randomperson

data class RandomPerson(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String
) {
    companion object
}
