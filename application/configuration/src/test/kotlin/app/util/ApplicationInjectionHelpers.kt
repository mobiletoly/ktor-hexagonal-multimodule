package app.util

import io.ktor.server.testing.TestApplicationEngine
import org.koin.ktor.ext.inject
import ports.input.TransactionService

fun TestApplicationEngine.transactionService() = application.inject<TransactionService>().value
