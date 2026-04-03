package com.paleblueapps.kmpcore.preferencesmanager

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsImplementation::class)
fun EncryptedPreferencesManager(
    fileName: String,
    json: Json = Json
): EncryptedPreferencesManager {
    val encryptedSettings = KeychainSettings(fileName)
    return RealEncryptedPreferencesManager(
        encryptedSettings = encryptedSettings,
        json = json
    )
}