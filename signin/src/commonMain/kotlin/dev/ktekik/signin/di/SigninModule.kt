package dev.ktekik.signin.di

import org.koin.core.KoinApplication
import org.koin.dsl.module

expect fun platformModule(): org.koin.core.module.Module

val commonModule = module {
    // Add other shared dependencies here (ViewModels, UseCases, etc.)
}

fun initSignInModule(koinApplication: KoinApplication) {
    koinApplication.modules(
        commonModule,
        platformModule() // Load the platform-specific module
    )
}