# Pale Blue KMP Core

A Kotlin Multiplatform (KMP) library for shared logic and common utilities across platforms (Android, iOS)

[![Maven Central](https://img.shields.io/maven-central/v/com.paleblueapps/kmpcore.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.paleblueapps/kmpcore)

## Features

-   **Networking:** A convenient layer for making network requests based on [Ktor](https://ktor.io/).
-   **Key-Value Storage:** A cross-platform solution for storing and retrieving key-value pairs based on [datastore](https://developer.android.com/topic/libraries/architecture/datastore) and [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings).
-   **KmmResult:** A type-safe wrapper for handling success and failure cases, inspired by [Kotlin Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/), with full type information preserved on all platforms.
-   **Currency Formatter:** A utility for formatting monetary values based on locale and currency code.
-   **Number Formatter:** A utility for formatting numbers based on locale.

## Getting Started

Add the library dependency to your `build.gradle.kts` or `build.gradle` file.
```kotlin
dependencies {
    implementation("com.paleblueapps:kmpcore:[latest-version]")
}
```

### Networking example
```kotlin
// Create ApiManager
val apiManager = ApiManager(
    baseUrl = "https://api.example.com/",
    enableLogging = true,
    defaultRequestConfig = {
        header("X-ID-device", "android")
        header("X-API-Version", "1")
    },
    responseValidator = { response ->
        when (response.status.value) {
            in 200..299 -> {}
            401 -> throw Error.UnauthorizedError
            in 400..499 -> throw Error.BackendResponseError
            else -> throw Error.BackendError
        }
    },
) // Or you can pass the custom Ktor client instance



// Usage
val endpoint = Endpoint("/users", HttpMethod.Get)
val userResult: Result<User> = apiManager.call(endpoint)
userResult.onSuccess { user ->
    println("User ID: ${user.id}, User Name: ${user.name}")
}.onFailure { exception ->
    println("Error: ${exception.message}")
}
```

### Key-value storage example
```kotlin
// Create PreferencesManager

// file: commonMain/PreferencesManager.kt
val preferencesFileName = "preferences.preferences_pb" // NOTE: this file extension should be preferences_pb
val encryptedPreferencesFileName = "encrypted_preferences"
expect val preferencesManager: PreferencesManager

// file: androidMain/PreferencesManager.kt
actual val preferencesManager = PreferencesManager(
    context = androidContext(), // pass the android context here either manually or using DI framework
    preferencesFileName = preferencesFileName,
    encryptedPreferencesFileName = encryptedPreferencesFileName,
)

// file: iosMain/PreferencesManager.kt
actual val preferencesManager = PreferencesManager(
    preferencesFileName = preferencesFileName,
    encryptedPreferencesFileName = encryptedPreferencesFileName,
)



// Encrypted storage example
preferencesManager.putEncryptedString("user_token", token)
val userToken = preferencesManager.getEncryptedString("user_token")

// Non encrypted storage example
preferencesManager.putString("user_id", token)
val userId = preferencesManager.getString("user_id")
```

### Currency formatter example
```kotlin
// Create CurrencyFormatter
val currencyFormatter = CurrencyFormatter()

// Usage
val formattedAmount = currencyFormatter.format(
    amount = 1234.56,
    currencyCode = "USD",
    withCurrencySymbol = true,
    minimumFractionDigits = 2,
    maximumFractionDigits = 2
) // "$1,234.56"
```

### Number formatter example
```kotlin
// Create NumberFormatter
val numberFormatter = NumberFormatter()

// Usage
val formattedNumber = numberFormatter.format(
    number = 1234567.89,
    localeCode = "en-US",
) // "1,234,567.89"
```