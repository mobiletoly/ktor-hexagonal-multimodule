package ports.input.util

// While some functions look very obvious, this class is still useful when we want to perform a unit test
// Just an example, HealthRoute might return a current timestamp and we want to validate that it is current
// In this case we will provide a mock DataService class that returns predefined currentTimeMillis()
// and it will allow us to test a response's timestamp field properly.
interface DateSupplier {
    fun currentTimeMillis(): Long
}
