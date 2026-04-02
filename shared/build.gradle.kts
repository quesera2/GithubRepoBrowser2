import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.metro)
    alias(libs.plugins.skie)
    alias(libs.plugins.mokoResources)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    android {
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
            export(projects.feature.common)
            export(projects.feature.resources)
            export(projects.feature.search)
            export(projects.feature.trending)
            export(projects.domain.model)
            export(projects.domain.contract)
            export(libs.moko.resources)
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
            implementation(projects.feature.common)
            implementation(projects.feature.resources)
            implementation(projects.feature.search)
            implementation(projects.feature.trending)
            implementation(projects.data.repository)
            implementation(projects.data.api)
            implementation(projects.data.apiKtor)
            implementation(projects.data.apiGraphql)
            implementation(projects.domain.contract)
            implementation(projects.domain.model)
            implementation(libs.metro.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.apollo.runtime)
        }
        androidMain.dependencies {
            implementation(libs.metro.viewmodel)
        }
        iosMain.dependencies {
            api(projects.feature.common)
            api(projects.feature.resources)
            api(projects.feature.search)
            api(projects.feature.trending)
            api(projects.domain.model)
            api(projects.domain.contract)
            api(libs.moko.resources)
        }
    }
}

buildkonfig {
    packageName = "que.sera.sera.githubbrowser2.shared"

    defaultConfigs {
        val token = gradleLocalProperties(rootDir, providers)
            .getProperty("github.token") ?: ""
        buildConfigField(FieldSpec.Type.STRING, "GITHUB_TOKEN", token)
    }
}