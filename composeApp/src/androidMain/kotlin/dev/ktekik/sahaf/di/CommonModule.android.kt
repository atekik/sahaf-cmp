package dev.ktekik.sahaf.di

import dev.ktekik.signin.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    viewModel { SignInViewModel() }
}
