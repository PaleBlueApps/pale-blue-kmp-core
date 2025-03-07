package com.paleblueapps.kmmcore.preferencesmanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private var dataStore: DataStore<Preferences>? = null
private var encryptedSettings: Settings? = null

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalForeignApi::class)
fun PreferencesManager(
    preferencesFileName: String,
    encryptedPreferencesFileName: String,
): PreferencesManager {
    if (dataStore == null) {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            produceFile = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                (requireNotNull(documentDirectory).path + "/$preferencesFileName").toPath()
            },
        )
    }
    if (encryptedSettings == null) {
        encryptedSettings = KeychainSettings(encryptedPreferencesFileName)
    }

    return RealPreferencesManager(
        datastore = dataStore!!,
        encryptedSettings = encryptedSettings!!,
    )
}
