package dev.ktekik.sahaf.di

import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.cloud.readerApiBuilder
import dev.ktekik.sahaf.datastore.ReaderIdRepository
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import dev.ktekik.sahaf.usecases.PostReaderUseCase
import dev.ktekik.signin.di.initSignInModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

expect fun platformModule(): org.koin.core.module.Module

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
val commonModule = module {
    single<HttpClient> {
        HttpClient(CIO.create { }) {
            install(ContentNegotiation) {
                json() // Use the Koin-managed Json instance
            }
        }
    }

    factory<ReaderApi> {
        readerApiBuilder(httpClient = get())
    }

    factory { PostReaderUseCase(readerApi = get()) }

    single { ReaderIdRepository(get()) }

    single { FtsNavigationViewModel() }

    factory { ReaderRegistryViewModel(postReaderUseCase = get(), readerIdRepository = get()) }
}

fun initKoin(config: KoinAppDeclaration = {}) {
    // Only initialize Koin if it hasn't been initialized yet
    if (GlobalContext.getOrNull() == null) {
        val koinApp = startKoin {
            config()
            modules(
                platformModule(), // Load the platform-specific module
                commonModule
            )
        }

        initSignInModule(koinApp)
    }
}
