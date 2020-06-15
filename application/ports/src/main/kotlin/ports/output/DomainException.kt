package ports.output

open class DomainException(
    val errorType: String,
    val title: String,
    val detail: String
) : RuntimeException(detail)

open class ResourceNotFoundException(
    title: String,
    detail: String
) : DomainException(errorType = ERROR_TYPE, title = title, detail = detail) {
    companion object {
        const val ERROR_TYPE = "/errors/resource-not-found"
    }
}
