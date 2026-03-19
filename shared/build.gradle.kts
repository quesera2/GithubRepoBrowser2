import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.metro)
    alias(libs.plugins.skie)
}

kotlin {
    androidLibrary {
        namespace = "que.sera.sera.githubbrowser2.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.feature.repoview)
            export(projects.domain.model)
            export(projects.domain.contract)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        languageSettings.enableLanguageFeature("ExpectActualClasses")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.repoview)
            implementation(projects.data.repository)
            implementation(projects.data.api)
            implementation(projects.domain.contract)
            implementation(projects.domain.model)
            implementation(libs.metro.runtime)
        }
        androidMain.dependencies {
            implementation(libs.metro.viewmodel)
        }
        iosMain.dependencies {
            api(projects.feature.repoview)
            api(projects.domain.model)
            api(projects.domain.contract)
        }
    }
}