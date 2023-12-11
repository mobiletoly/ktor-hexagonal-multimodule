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
        AddPersonService(addPersonPort = get(), txPort = get())
    }
    single<LoadPersonUsecase> {
        LoadPersonService(loadPersonPort = get(), txPort = get())
    }
    single<DeletePersonUsecase> {
        DeletePersonService(deletePersonPort = get(), txPort = get())
    }
    single<UpdatePersonUsecase> {
        UpdatePersonService(updatePersonPort = get(), txPort = get())
    }
    single<LoadAllPersonsUsecase> {
        LoadAllPersonsService(loadAllPersonsPort = get(), txPort = get())
    }

    single<PopulateRandomPersonUsecase> {
        RandomPersonService(
            generateRandomPersonPort = get(),
            addPersonPort = get(),
            txPort = get(),
        )
    }
}
