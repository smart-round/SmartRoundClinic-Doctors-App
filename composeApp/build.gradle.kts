import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlinSerialization)
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Force kotlinx-datetime to a single version across every target (Android + iOS).
// Without this, iOS transitively resolves a newer version than Android's declared
// 0.6.2 — and RealtimeKit's compiled Android bytecode hard-references
// kotlinx.datetime.Clock$System as a real class, which newer kotlinx-datetime
// versions restructure away, crashing with NoClassDefFoundError at runtime the
// first time the SDK logs anything.
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.get()}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        // The release framework link (Xcode Archive) OOMs specifically inside
        // DevirtualizationAnalysis's constraint-graph build — Firebase/RealtimeKit/
        // KMPNotifier's combined ObjC interop surface makes the call graph too large for
        // this pass to fit in any heap that also leaves room for Xcode on a 16GB Mac.
        // Only disable the phase that actually OOMs; leaving EscapeAnalysis/BuildDFG/DCE
        // enabled avoids a release-only SIGSEGV (raw `ldr [x8]` on x8=8, a corrupted
        // direct-TSD-style thread-local access) caused by those passes running against
        // state EscapeAnalysis didn't get to normalize.
        iosTarget.compilerOptions {
            freeCompilerArgs.add("-Xdisable-phases=DevirtualizationAnalysis,Devirtualization,RemoveRedundantCallsToStaticInitializersPhase")
        }
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.kmpnotifier)
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.ui.tooling)
            // BackHandler — used to redirect the system back gesture during an active call
            // into a minimize instead of letting it fall through to a nav-backstack pop.
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            // Cloudflare RealtimeKit Core SDK — call logic only, no prebuilt UI.
            // We own the call screen (see presentation/main/chat/call/) so we can
            // support background continuity and a foreground-service notification,
            // neither of which the UI Kit's KeepAliveService actually provides in
            // the version we were previously pinned to.
            implementation("com.cloudflare.realtimekit:core-android:2.1.0")
        }
        commonMain.dependencies {
            implementation(libs.media.player)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.client.websockets)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kvault)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)
            implementation(libs.napier)
            implementation(libs.kotlinx.datetime)
            implementation(libs.richeditor.compose)
            implementation(libs.compose.webview)
            implementation(libs.coil.svg)
            api(libs.kmpnotifier)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "ke.co.smartroundclinic.doctor"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
}
