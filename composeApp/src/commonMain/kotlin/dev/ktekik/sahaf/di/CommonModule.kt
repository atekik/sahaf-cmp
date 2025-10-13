package dev.ktekik.sahaf.di

import dev.ktekik.signin.di.initSignInModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

expect fun platformModule(): org.koin.core.module.Module

val commonModule = module {
    // Add other shared dependencies here (ViewModels, UseCases, etc.)
}

fun initKoin() {
    val koinApp = startKoin {
        modules(
            commonModule,
            platformModule() // Load the platform-specific module
        )
    }

    initSignInModule(koinApp)
}
