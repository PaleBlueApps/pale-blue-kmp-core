import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.serialization.json)

            // multiplatform settings
            implementation(libs.multiplatform.settings)
            implementation(libs.androidx.datastore.preferences.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.security.crypto)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.paleblueapps.kmmcore"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Stub secrets to let the project sync and build without the publication values set up
extra["signing.keyId"] = null
extra["signing.password"] = null
extra["signing.secretKeyRingFile"] = null
extra["mavenCentralUsername"] = null
extra["mavenCentralPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply { load(it) }
    }.onEach { (name, value) ->
        extra[name.toString()] = value
    }
} else {
    extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    extra["signing.password"] = System.getenv("SIGNING_PASSWORD")
    extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    extra["mavenCentralUsername"] = System.getenv("MAVEN_CENTRAL_USERNAME")
    extra["mavenCentralPassword"] = System.getenv("MAVEN_CENTRAL_PASSWORD")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(
        groupId = "com.paleblueapps",
        artifactId = "kmmcore",
        version = "1.0.0"
    )

    pom {
        name = "PaleBlueKmmCore"
        description = "A Kotlin Multiplatform (KMP) library for shared logic and common utilities across platforms"
        url = "https://github.com/paleblueapps/pale-blue-kmm-core"

        developers {
            developer {
                id = "paleblueapps"
                name = "Pale Blue"
            }
        }
        scm {
            url = "https://github.com/paleblueapps/pale-blue-kmm-core"
        }
    }
}
