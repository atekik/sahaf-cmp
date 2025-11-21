package dev.ktekik.sahaf.android

import android.app.Application
import dev.ktekik.sahaf.di.initKoin

class SahafApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }
}