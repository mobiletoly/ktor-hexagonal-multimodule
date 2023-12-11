package adapters.remoting

import adapters.remoting.randomperson.RandomPersonAdapter
import core.outport.GenerateRandomPersonPort
import core.outport.GetRandomPersonServiceConfigPort
import org.koin.dsl.module

val remotingModule = module {
    single<GenerateRandomPersonPort> {
        RandomPersonAdapter(config = get<GetRandomPersonServiceConfigPort>().randomPersonService)
    }
}
