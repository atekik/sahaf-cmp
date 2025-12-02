package dev.ktekik.sahaf.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.ktekik.sahaf.datastore.createDataStore
import dev.ktekik.signin.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    viewModel { SignInViewModel() }

    single<DataStore<Preferences>> { createDataStore(get<Context>()) }
}
