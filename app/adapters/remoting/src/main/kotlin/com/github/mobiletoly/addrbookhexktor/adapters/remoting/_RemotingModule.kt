package com.github.mobiletoly.addrbookhexktor.adapters.remoting

import com.github.mobiletoly.addrbookhexktor.adapters.remoting.randomperson.RandomPersonAdapter
import org.koin.dsl.module

val remotingModule = module {
    single {
        RandomPersonAdapter(config = get())
    }
}
