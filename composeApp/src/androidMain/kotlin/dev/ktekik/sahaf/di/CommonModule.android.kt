package dev.ktekik.sahaf.di

import dev.ktekik.signin.SignInViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    viewModel { SignInViewModel() }

    factory<HttpClientEngine> { OkHttpEngine(OkHttpConfig()) }
}
