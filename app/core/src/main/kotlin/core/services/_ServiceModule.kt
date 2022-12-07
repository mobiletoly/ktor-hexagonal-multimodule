package core.services

import core.usecase.AddPersonUsecase
import core.usecase.DeletePersonUsecase
import core.usecase.HealthStatusUsecase
import core.usecase.LoadAllPersonsUsecase
import core.usecase.LoadPersonUsecase
import core.usecase.PopulateRandomPersonUsecase
import core.usecase.UpdatePersonUsecase
import org.koin.dsl.module

internal val serviceModule = module {
    single<HealthStatusUsecase> {
        HealthStatusService(getDeploymentPort = get())
    }

    single<AddPersonUsecase> {
        AddPersonService(txPort = get(), addPersonPort = get())
    }
    single<LoadPersonUsecase> {
        LoadPersonService(txPort = get(), loadPersonPort = get())
    }
    single<DeletePersonUsecase> {
        DeletePersonService(txPort = get(), deletePersonPort = get())
    }
    single<UpdatePersonUsecase> {
        UpdatePersonService(txPort = get(), updatePersonPort = get())
    }
    single<LoadAllPersonsUsecase> {
        LoadAllPersonsService(txPort = get(), loadAllPersonsPort = get())
    }

    single<PopulateRandomPersonUsecase> {
        RandomPersonService(
            generateRandomPersonPort = get(),
            txPort = get(),
            addPersonPort = get(),
        )
    }
}
