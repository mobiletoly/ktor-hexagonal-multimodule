package adapters.env

import core.outport.GetDatabaseConfigPort
import core.outport.GetDeploymentConfigPort
import core.outport.GetDeploymentEnvPort
import core.outport.GetRandomPersonServiceConfigPort
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
