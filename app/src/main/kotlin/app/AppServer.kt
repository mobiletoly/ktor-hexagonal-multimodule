package app

import adapters.adapterModule
import adapters.envModule
import domain.domainModule
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger

@Suppress("unused")
fun Application.module() {
    // Add Koin DI support to Ktor
    install(Koin) {
        SLF4JLogger()
        properties(mapOf("application" to this@module))
        modules(envModule, adapterModule, domainModule)
    }
}
