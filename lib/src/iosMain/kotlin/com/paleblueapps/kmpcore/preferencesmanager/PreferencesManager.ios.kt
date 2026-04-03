package com.paleblueapps.kmpcore.preferencesmanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private val dataStoresByPath = mutableMapOf<String, DataStore<Preferences>>()

@OptIn(
    ExperimentalSettingsImplementation::class,
    ExperimentalForeignApi::class,
    ExperimentalSettingsApi::class
)
fun PreferencesManager(
    fileName: String,
    json: Json = Json
): PreferencesManager {
    require(fileName.endsWith(".preferences_pb")) {
        "Preferences file name must end with '.preferences_pb', got: '$fileName'"
    }
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val filePath = requireNotNull(documentDirectory).path + "/$fileName"
    val dataStore = dataStoresByPath.getOrPut(filePath) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { filePath.toPath() },
        )
    }

    return RealPreferencesManager(
        settings = DataStoreSettings(dataStore),
        json = json,
    )
}
