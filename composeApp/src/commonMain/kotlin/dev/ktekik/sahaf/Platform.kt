package dev.ktekik.sahaf

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun HideStatusBarComposable()

@Composable
expect fun ShowStatusBarComposable()