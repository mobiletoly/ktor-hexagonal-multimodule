package adapters.remoting

import adapters.remoting.randomperson.RandomPersonAdapter
import org.koin.dsl.module

val remotingModule = module {
    single {
        RandomPersonAdapter(config = get())
    }
}
