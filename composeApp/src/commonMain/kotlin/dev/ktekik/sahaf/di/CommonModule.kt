package dev.ktekik.sahaf.di

import org.koin.core.context.startKoin
import org.koin.dsl.module

expect fun platformModule(): org.koin.core.module.Module

val commonModule = module {
    // Add other shared dependencies here (ViewModels, UseCases, etc.)
}


fun initKoin() {
    startKoin {
        modules(
            commonModule,
            platformModule() // Load the platform-specific module
        )
    }
}