package com.github.mobiletoly.addrbookhexktor.adapters.env

import com.github.mobiletoly.addrbookhexktor.outport.GetDatabaseConfigPort
import com.github.mobiletoly.addrbookhexktor.outport.GetDeploymentConfigPort
import com.github.mobiletoly.addrbookhexktor.outport.GetDeploymentEnvPort
import com.github.mobiletoly.addrbookhexktor.outport.GetRandomPersonServiceConfigPort
import org.koin.dsl.binds
import org.koin.dsl.module

val envModule = module() {
    single<GetDeploymentEnvPort> {
        AppOsEnvironment()
    }

    single {
        AppConfig(get<GetDeploymentEnvPort>().deploymentEnv)
    } binds arrayOf(
        GetDeploymentConfigPort::class,
        GetDatabaseConfigPort::class,
        GetRandomPersonServiceConfigPort::class,
    )
}
