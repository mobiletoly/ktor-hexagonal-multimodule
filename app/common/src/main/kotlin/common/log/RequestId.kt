package common.log

import org.slf4j.MDC

const val xRequestIdLogKey = "xRequestId"

/**
 * Get request id. This is useful for traceability to pass it into X-Request-Id header when send
 * requests between microservices.
 */
fun xRequestId(): String {
    return MDC.get(xRequestIdLogKey) ?: ""
}

fun setXRequestId(id: String?) {
    MDC.put(xRequestIdLogKey, id)
}
