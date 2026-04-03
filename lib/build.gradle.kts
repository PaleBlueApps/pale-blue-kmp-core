import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    android {
        namespace = "com.paleblueapps.kmpcore"
        compileSdk = 36
        minSdk = 24

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            // ktor
            api(libs.ktor.client.core)
            api(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // multiplatform settings
            implementation(libs.multiplatform.settings)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.ktx)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.security.crypto)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(
        groupId = "com.paleblueapps",
        artifactId = "kmpcore",
        version = "1.4.0"
    )

    pom {
        name = "PaleBlueKmmCore"
        description = "A Kotlin Multiplatform (KMP) library for shared logic and common utilities across platforms"
        url = "https://github.com/paleblueapps/pale-blue-kmp-core"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "paleblueapps"
                name = "Pale Blue"
            }
        }
        scm {
            url = "https://github.com/paleblueapps/pale-blue-kmp-core"
        }
    }
}
