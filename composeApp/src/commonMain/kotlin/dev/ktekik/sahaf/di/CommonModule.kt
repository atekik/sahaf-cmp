package dev.ktekik.sahaf.di

import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.cloud.bookApiBuilder
import dev.ktekik.sahaf.cloud.readerApiBuilder
import dev.ktekik.sahaf.datastore.ReaderRepository
import dev.ktekik.sahaf.getBaseUrl
import dev.ktekik.sahaf.home.HomeViewModel
import dev.ktekik.sahaf.listing.BookListingViewModel
import dev.ktekik.sahaf.listing.IsbnQueryViewModel
import dev.ktekik.sahaf.listing.ListingDetailViewModel
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import dev.ktekik.sahaf.usecases.FetchListingsWithShippingUseCase
import dev.ktekik.sahaf.usecases.FetchReaderIdUseCase
import dev.ktekik.sahaf.usecases.FetchReaderIdZipcodePairUseCase
import dev.ktekik.sahaf.usecases.CreateBookListingUseCase
import dev.ktekik.sahaf.usecases.IsbnQueryUseCase
import dev.ktekik.sahaf.usecases.PostReaderUseCase
import dev.ktekik.sahaf.usecases.QueryBookListingByIdUseCase
import dev.ktekik.sahaf.usecases.QueryReaderUseCase
import dev.ktekik.sahaf.usecases.SaveReaderIdUseCase
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
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json() // Use the Koin-managed Json instance
            }
        }
    }

    factory<ReaderApi> {
        readerApiBuilder(httpClient = get(), baseUrl = getBaseUrl())
    }

    factory<BookApi> {
        bookApiBuilder(httpClient = get(), baseUrl = getBaseUrl())
    }

    factory { PostReaderUseCase(readerApi = get()) }

    factory { QueryReaderUseCase(readerApi = get()) }

    single { ReaderRepository(get()) }

    factory { FetchReaderIdUseCase(get()) }

    factory { FetchReaderIdZipcodePairUseCase(get()) }

    factory { SaveReaderIdUseCase(readerRepository = get()) }

    factory { FetchListingsWithShippingUseCase(bookApi = get(), readerApi = get()) }

    factory { IsbnQueryUseCase(bookApi = get()) }

    factory { CreateBookListingUseCase(bookApi = get()) }

    factory { QueryBookListingByIdUseCase(bookApi = get()) }

    single { NavigationViewModel(get()) }

    single { HomeViewModel(get(), get()) }

    factory { ReaderRegistryViewModel(postReaderUseCase = get(), saveReaderIdUseCase = get()) }

    factory { IsbnQueryViewModel(isbnQueryUseCase = get()) }

    single { BookListingViewModel(createBookListingUseCase = get(), fetchReaderIdZipcodePairUseCase = get()) }

    factory { ListingDetailViewModel(queryBookListingByIdUseCase = get(), readerIdZipcodePairUseCase = get()) }
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
