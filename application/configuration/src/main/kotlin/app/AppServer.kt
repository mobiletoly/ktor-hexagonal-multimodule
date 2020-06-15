package app

import adapters.adapterModule
import adapters.envModule
import core.coreModule
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger

@Suppress("unused")
fun Application.module() {
    // Add Koin DI support to Ktor
    install(Koin) {
        SLF4JLogger()
        modules(
            module {
                single { this@module }
            },
            envModule,
            adapterModule,
            coreModule
        )
    }
}
