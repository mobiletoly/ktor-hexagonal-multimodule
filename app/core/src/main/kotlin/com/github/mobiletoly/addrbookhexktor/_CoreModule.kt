package com.github.mobiletoly.addrbookhexktor

import com.github.mobiletoly.addrbookhexktor.usecase.usecaseModule
import org.koin.dsl.module

val coreModule = module {
    includes(usecaseModule)
}
