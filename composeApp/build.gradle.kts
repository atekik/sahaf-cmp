import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaTarget.get()))
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "composeApp"
            isStatic = true
        }
        pod("FirebaseCore") { linkOnly = true }
        pod("GoogleSignIn") { linkOnly = true }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.navigation)
            implementation(project(":utils"))
            implementation(project(":signin"))
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.orbit)
            implementation(libs.bundles.koin)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.cio)

            implementation(libs.data.store.core)
            implementation(libs.data.store.preferences)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.coil)
        }
        iosMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.ktekik.sahaf"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.javaSource.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.javaSource.get())
    }
}