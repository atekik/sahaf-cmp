import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.playServices)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.ktekik.sahaf.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "dev.ktekik.sahaf.android"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    signingConfigs {
        create("sahafDebug") {
            storeFile = file("~/debugSahaf")
            storePassword = "Jul14N03lT3k1k#9"
            keyAlias = "key0"
            keyPassword = "LucyK414T3k1k#9"
        }
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.crashlyticsKtx)
    implementation(libs.bundles.android.compose)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
}