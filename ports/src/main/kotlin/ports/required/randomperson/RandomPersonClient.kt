package ports.required.randomperson

interface RandomPersonClient {
    suspend fun fetchRandomPerson(): RandomPerson
}
