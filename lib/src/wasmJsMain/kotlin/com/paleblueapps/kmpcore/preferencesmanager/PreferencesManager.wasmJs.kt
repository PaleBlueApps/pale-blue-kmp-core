package com.paleblueapps.kmpcore.preferencesmanager

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class)
fun PreferencesManager(
    fileName: String,
    json: Json = Json
): PreferencesManager {
    require(fileName.endsWith(".preferences_pb")) {
        "Preferences file name must end with '.preferences_pb', got: '$fileName'"
    }

    return RealPreferencesManager(
        settings = StorageSettings().makeObservable().toFlowSettings(),
        json = json,
    )
}
