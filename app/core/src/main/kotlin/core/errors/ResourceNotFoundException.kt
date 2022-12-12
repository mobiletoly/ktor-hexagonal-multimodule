package core.errors

open class ResourceNotFoundException(
    title: String,
    detail: String,
    specifics: Map<String, String>,
) : DomainException(errorType = ERROR_TYPE, title = title, detail = detail, specifics = specifics) {
    companion object {
        const val ERROR_TYPE = "/errors/resource-not-found"
    }
}
