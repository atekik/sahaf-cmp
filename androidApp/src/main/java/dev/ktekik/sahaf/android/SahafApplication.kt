package dev.ktekik.sahaf.android

import android.app.Application
import dev.ktekik.sahaf.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SahafApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin with Android-specific configuration
        initKoin {
            androidLogger() // Logger for Koin events
            androidContext(this@SahafApplication) // Provide application context
        }
    }
}
