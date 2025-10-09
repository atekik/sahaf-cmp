package dev.ktekik.utils

import androidx.compose.runtime.compositionLocalOf

val LocalResources = compositionLocalOf<Resources> {
    error("No LocalResources provided")
}