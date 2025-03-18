package com.paleblueapps.kmpcore.preferencesmanager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import okio.Path.Companion.toPath

private var dataStore: DataStore<Preferences>? = null
private var encryptedSettings: Settings? = null

fun PreferencesManager(
    context: Context,
    preferencesFileName: String,
    encryptedPreferencesFileName: String,
): PreferencesManager {
    if (dataStore == null) {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            produceFile = { context.filesDir.resolve(preferencesFileName).absolutePath.toPath() },
        )
    }
    if (encryptedSettings == null) {
        encryptedSettings = SharedPreferencesSettings(
            delegate = EncryptedSharedPreferences.create(
                encryptedPreferencesFileName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            ),
        )
    }

    return RealPreferencesManager(
        datastore = dataStore!!,
        encryptedSettings = encryptedSettings!!,
    )
}
