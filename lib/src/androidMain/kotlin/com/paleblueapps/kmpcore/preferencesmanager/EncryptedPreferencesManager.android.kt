package com.paleblueapps.kmpcore.preferencesmanager

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.serialization.json.Json

fun EncryptedPreferencesManager(
    context: Context,
    fileName: String,
    json: Json = Json
): EncryptedPreferencesManager {
    val encryptedSettings = SharedPreferencesSettings(
        delegate = EncryptedSharedPreferences.create(
            fileName,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        ),
    )

    return RealEncryptedPreferencesManager(
        encryptedSettings = encryptedSettings,
        json = json
    )
}