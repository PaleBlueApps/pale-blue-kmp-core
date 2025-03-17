import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
    signing
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
extra["ossrhUsername"] = null
extra["ossrhPassword"] = null

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
    extra["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    extra["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

publishing {
    publications.withType<MavenPublication> {
        groupId = "com.paleblueapps"
        artifactId = "kmmcore"
        version = "1.0"

        pom {
            name.set("PaleBlueKmmCore")
            description.set("A Kotlin Multiplatform library")
            url.set("https://github.com/paleblueapps/pale-blue-kmm-core")

            licenses {
//                license {
//                    name.set("The Apache Software License, Version 2.0")
//                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    distribution.set("repo")
//                }
            }
            developers {
                developer {
                    id.set("paleblueapps")
                    name.set("Pale Blue Apps")
                }
            }
            scm {
                url.set("https://github.com/paleblueapps/pale-blue-kmm-core")
            }
        }

        repositories {
            maven {
                name = "sonatypeSnapshot"
                setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = extra["ossrhUsername"] as? String
                    password = extra["ossrhPassword"] as? String
                }
            }

            maven {
                name = "sonatype"
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = extra["ossrhUsername"] as? String
                    password = extra["ossrhPassword"] as? String
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}
