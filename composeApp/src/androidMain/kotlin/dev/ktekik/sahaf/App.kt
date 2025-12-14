package dev.ktekik.sahaf

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import dev.ktekik.sahaf.navigation.FtsNavHost

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true) // Enable crossfade animation by default
            .logger(DebugLogger()) // Helps debug why images fail on specific platforms
            .build()
    }

    FtsNavHost()
}