package shared.util

import com.github.michaelbull.logging.InlineLogger

fun InlineLogger.d(head: String? = null, msg: () -> Any?) =
    this.debug { "${head(head)}${msg()}" }

fun InlineLogger.d(head: String? = null, t: Throwable, msg: () -> Any?) =
    this.debug(t) { "${head(head)}${msg()}" }

fun InlineLogger.i(head: String? = null, msg: () -> Any?) =
    this.info { "${head(head)}${msg()}" }

fun InlineLogger.i(head: String? = null, t: Throwable, msg: () -> Any?) =
    this.info(t) { "${head(head)}${msg()}" }

fun InlineLogger.w(head: String? = null, msg: () -> Any?) =
    this.warn { "${head(head)}${msg()}" }

fun InlineLogger.w(head: String? = null, t: Throwable, msg: () -> Any?) =
    this.warn(t) { "${head(head)}${msg()}" }

fun InlineLogger.e(head: String? = null, msg: () -> Any?) =
    this.error { "${head(head)}${msg()}" }

fun InlineLogger.e(head: String? = null, t: Throwable, msg: () -> Any?) =
    this.error(t) { "${head(head)}${msg()}" }

private fun head(head: String?) = if (head == null) "" else "$head : "
