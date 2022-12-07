package core.errors

open class ResourceAlreadyExistsException(
    title: String,
    detail: String
) : DomainException(errorType = ERROR_TYPE, title = title, detail = detail, specifics = null) {
    companion object {
        const val ERROR_TYPE = "/errors/resource-already-exists"
    }
}
