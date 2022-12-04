package com.github.mobiletoly.addrbookhexktor.core.service

import com.github.mobiletoly.addrbookhexktor.usecase.HealthStatusUsecase
import org.koin.dsl.module

internal val serviceModule = module {
    single<HealthStatusUsecase> {
        HealthStatusService(deploymentPort = get())
    }
}
