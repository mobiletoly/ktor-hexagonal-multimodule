package common.log

import org.slf4j.MDC

const val X_REQUEST_ID_LOG_KEY = "xRequestId"

/**
 * Get request id. This is useful for traceability to pass it into X-Request-Id header when send
 * requests between microservices.
 */
fun xRequestId(): String {
    return MDC.get(X_REQUEST_ID_LOG_KEY) ?: ""
}

fun setXRequestId(id: String?) {
    MDC.put(X_REQUEST_ID_LOG_KEY, id)
}
