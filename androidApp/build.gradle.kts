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
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "dev.ktekik.sahaf.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.javaSource.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.javaSource.get())
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.javaTarget.get())
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