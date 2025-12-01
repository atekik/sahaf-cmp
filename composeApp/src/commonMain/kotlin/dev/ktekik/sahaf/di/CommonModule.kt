package dev.ktekik.sahaf.di

import dev.ktekik.sahaf.cloud.PostReaderUseCase
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.cloud.ReaderApiImpl
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import dev.ktekik.signin.di.initSignInModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

expect fun platformModule(): org.koin.core.module.Module

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
val commonModule = module {
    // Add other shared dependencies here (ViewModels, UseCases, etc.)

    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    factory<ReaderApi> {
        ReaderApiImpl(httpClient = get())
    }

    factory { PostReaderUseCase(readerApi = get()) }

    factory { ReaderRegistryViewModel(postReaderUseCase = get()) }

    // NavigationViewModel - singleton to maintain navigation state
    single { FtsNavigationViewModel() }
}

fun initKoin() {
    // Only initialize Koin if it hasn't been initialized yet
    if (GlobalContext.getOrNull() == null) {
        val koinApp = startKoin {
            modules(
                platformModule(), // Load the platform-specific module
                commonModule
            )
        }

        initSignInModule(koinApp)
    }
}
