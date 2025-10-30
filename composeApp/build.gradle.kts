import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.2.20"
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Add bundle ID to resolve warning
            binaryOption("bundleId", "com.developerstring.nexpay")

            // Link CoreNFC framework for iOS NFC functionality
            linkerOpts += "-framework"
            linkerOpts += "CoreNFC"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.biometric)
            implementation(libs.androidx.security.crypto)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.material.icons.extended)

            implementation(libs.kmpalette.core)

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            implementation("xyz.mcxross.kaptos:kaptos:0.1.2-beta")
            implementation("network.chaintech:qr-kit:1.0.3")
            implementation("io.github.alexzhirkevich:qrose:1.0.1")

            // ktor bundle
            implementation(libs.bundles.ktor)

            // image loading library
            implementation(libs.coil.compose.core)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose)

            implementation("io.github.thechance101:chart:Beta-0.0.5")

            // kmp for math for kmp
            implementation("com.ionspin.kotlin:bignum:0.3.10")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.developerstring.nexpay"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.developerstring.nexpay"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    // Only add Room compiler for Android target
    add("kspAndroid", libs.androidx.room.compiler)
//    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
//    add("kspIosX64", libs.androidx.room.compiler)
//    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

//ksp.useKsp2 = false