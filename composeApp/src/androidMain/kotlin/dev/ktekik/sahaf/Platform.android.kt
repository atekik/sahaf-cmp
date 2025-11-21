package dev.ktekik.sahaf

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun HideStatusBarComposable() {
    val view = LocalView.current
    val window = (view.context as Activity).window

    SideEffect {
        WindowInsetsControllerCompat(window, view).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
actual fun ShowStatusBarComposable() {
    val view = LocalView.current
    val window = (view.context as Activity).window

    SideEffect {
        WindowInsetsControllerCompat(window, view).apply {
            show(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}