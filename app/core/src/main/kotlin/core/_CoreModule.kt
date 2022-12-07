package core

import core.services.serviceModule
import org.koin.dsl.module

val coreModule = module {
    includes(serviceModule)
}
