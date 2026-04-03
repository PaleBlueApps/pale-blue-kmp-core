package com.paleblueapps.kmpcore.preferencesmanager

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
fun PreferencesManager(
    context: Context,
    fileName: String,
    json: Json = Json
): PreferencesManager {
    require(fileName.endsWith(".preferences_pb")) {
        "Preferences file name must end with '.preferences_pb', got: '$fileName'"
    }

    val dataStore = PreferenceDataStoreFactory.createWithPath(
        produceFile = { context.filesDir.resolve(fileName).absolutePath.toPath() },
    )

    return RealPreferencesManager(
        settings = DataStoreSettings(dataStore),
        json = json
    )
}
