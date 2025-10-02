package dev.ktekik.sahaf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform