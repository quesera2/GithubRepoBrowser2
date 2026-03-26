import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

kotlin {
    android {
        namespace = "que.sera.sera.githubbrowser2.feature.repoview"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(projects.domain.contract)
            implementation(projects.feature.common)
            implementation(projects.feature.resources)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.metro.runtime)
            implementation(libs.moko.resources)
        }
        commonTest.dependencies {
            implementation(projects.feature.testSupport)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
