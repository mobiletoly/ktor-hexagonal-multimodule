package com.github.mobiletoly.addrbookhexktor

import com.github.mobiletoly.addrbookhexktor.core.service.serviceModule
import org.koin.dsl.module

val coreModule = module {
    includes(serviceModule)
}
