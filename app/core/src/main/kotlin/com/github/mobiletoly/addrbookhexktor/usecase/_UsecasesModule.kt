package com.github.mobiletoly.addrbookhexktor.usecase

import org.koin.dsl.module

internal val usecaseModule = module {
    single<GetHealthStatus> {
        GetHealthStatusImpl(deploymentPort = get())
    }
}
