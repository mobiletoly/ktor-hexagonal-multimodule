package adapters.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

val sharedJsonMapper = jacksonObjectMapper().apply {
    setProjectDefaults()
}

fun ObjectMapper.setProjectDefaults() {
    dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        // This is the key line which converts the date to UTC which cannot be accessed with the default serializer
        timeZone = TimeZone.getTimeZone("UTC")
    }
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    enable(SerializationFeature.INDENT_OUTPUT)
}

fun ObjectMapper.convertToString(src: Any) = writeValueAsString(src)!!

fun String?.fromJsonToMap() = this?.let { str ->
    sharedJsonMapper.readValue<Map<String, Any>>(str)
}
