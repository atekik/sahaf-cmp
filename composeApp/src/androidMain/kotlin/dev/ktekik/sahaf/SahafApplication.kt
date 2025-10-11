package dev.ktekik.sahaf

import android.app.Application
import dev.ktekik.sahaf.di.initKoin

class SahafApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }
}