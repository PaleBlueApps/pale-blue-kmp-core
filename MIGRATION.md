# Migration Guide

## Migrating to 2.0.0

Version `2.0.0` contains a breaking change to key-value storage APIs.

Previously, `PreferencesManager` handled both regular and encrypted values. In `2.0.0`, these responsibilities are split into separate types:

- `PreferencesManager` for non-encrypted storage
- `EncryptedPreferencesManager` for encrypted storage
- `BasePreferencesManager` for shared APIs

## What changed

### Before 2.0.0
A single `PreferencesManager` instance was created with both regular and encrypted storage configuration:

```kotlin
val preferencesManager = PreferencesManager(
    context = androidContext(),
    preferencesFileName = "preferences.preferences_pb",
    encryptedPreferencesFileName = "encrypted_preferences",
)

preferencesManager.putString("user_id", userId)
val storedUserId = preferencesManager.getString("user_id")

preferencesManager.putEncryptedString("user_token", token)
val userToken = preferencesManager.getEncryptedString("user_token")
```

### In 2.0.0
You now create two managers and use each one explicitly:

```kotlin
val preferencesManager = PreferencesManager(
    context = androidContext(),
    fileName = "preferences.preferences_pb",
)

val encryptedPreferencesManager = EncryptedPreferencesManager(
    context = androidContext(),
    fileName = "encrypted_preferences",
)

preferencesManager.putString("user_id", userId)
val storedUserId = preferencesManager.getString("user_id")

encryptedPreferencesManager.putString("user_token", token)
val userToken = encryptedPreferencesManager.getString("user_token")
```

## Required code changes

### 1. Replace constructor usage

#### Android

Before:
```kotlin
PreferencesManager(
    context = context,
    preferencesFileName = "preferences.preferences_pb",
    encryptedPreferencesFileName = "encrypted_preferences",
)
```

After:
```kotlin
PreferencesManager(
    context = context,
    fileName = "preferences.preferences_pb",
)

EncryptedPreferencesManager(
    context = context,
    fileName = "encrypted_preferences",
)
```

#### iOS

Before:
```kotlin
PreferencesManager(
    preferencesFileName = "preferences.preferences_pb",
    encryptedPreferencesFileName = "encrypted_preferences",
)
```

After:
```kotlin
PreferencesManager(
    fileName = "preferences.preferences_pb",
)

EncryptedPreferencesManager(
    fileName = "encrypted_preferences",
)
```

### 2. Replace encrypted API calls

Before:
```kotlin
preferencesManager.putEncryptedString("user_token", token)
val userToken = preferencesManager.getEncryptedString("user_token")
```

After:
```kotlin
encryptedPreferencesManager.putString("user_token", token)
val userToken = encryptedPreferencesManager.getString("user_token")
```

Apply the same pattern to any encrypted boolean, int, long, float, double, or object storage calls.

### 3. Keep regular storage on `PreferencesManager`

Regular storage calls remain on `PreferencesManager`:

```kotlin
preferencesManager.putString("user_id", userId)
val storedUserId = preferencesManager.getString("user_id")
```

### 4. Shared abstractions

If your code depends on the shared non-flow API surface only, use `BasePreferencesManager`.

- `PreferencesManager` extends `BasePreferencesManager`
- `EncryptedPreferencesManager` extends `BasePreferencesManager`

`PreferencesManager` still provides flow-based APIs such as:

```kotlin
preferencesManager.getStringAsFlow("user_id")
preferencesManager.getObjectAsFlow<MyType>("user")
```

These flow APIs are not available on `EncryptedPreferencesManager`.

## Notes

- On Android and iOS, `PreferencesManager` still requires a file ending in `.preferences_pb`
- `EncryptedPreferencesManager` uses a separate file/keychain name
- WasmJs supports `PreferencesManager`, but not `EncryptedPreferencesManager`

## Checklist

- [ ] Update dependency to `2.0.0`
- [ ] Create a separate `PreferencesManager` for regular storage
- [ ] Create a separate `EncryptedPreferencesManager` for encrypted storage
- [ ] Replace `putEncrypted*` / `getEncrypted*` usage with calls on `EncryptedPreferencesManager`
- [ ] Use `BasePreferencesManager` where only the shared API surface is needed
