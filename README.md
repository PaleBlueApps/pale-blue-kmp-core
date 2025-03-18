# Paleblue-KmmCore (KMM)

A Kotlin Multiplatform (KMP) library for shared logic and common utilities across platforms

## Features

-   **Networking:** A convenient layer for making network requests based on [Ktor](https://ktor.io/).
-   **Key-Value Storage:** A cross-platform solution for storing and retrieving key-value pairs based on [datastore](https://developer.android.com/topic/libraries/architecture/datastore) and [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings).

## Getting Started

Add the library dependency to your `build.gradle.kts` or `build.gradle` file.
```kotlin
dependencies {
    api("com.paleblueapps:kmm-core:1.0.0")
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
)



// Usage
val endpoint = Endpoint("/users", HttpMethod.Get)
val userResult: Result<User> = call(endpoint)
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
val preferencesFileName = "preferences.pb"
val encryptedPreferencesFileName = "encrypted_preferences.pb"
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