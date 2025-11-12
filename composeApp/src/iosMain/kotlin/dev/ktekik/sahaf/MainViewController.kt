package dev.ktekik.sahaf

import androidx.compose.ui.window.ComposeUIViewController
import dev.ktekik.sahaf.di.initKoin
import dev.ktekik.sahaf.theming.SahafTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize

fun MainViewController() = ComposeUIViewController {
    // Initialize Koin for iOS
    initKoin()
    
    SahafTheme {
        Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost()
        }
    }
}