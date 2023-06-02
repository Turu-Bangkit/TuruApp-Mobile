package com.capstone.turuappmobile.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val SLEEP_PREFERENCES_NAME = "sleep_preferences"


class SleepSubscriptionStatus(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SUBSCRIBED_TO_SLEEP_DATA = booleanPreferencesKey("subscribed_to_sleep_data")
        val TOTALONRECEIVESLEEP = intPreferencesKey("total_on_receive_sleep")
    }

    val subscribedToSleepDataFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] ?: false
    }

    val totalOnReceiveSleep: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TOTALONRECEIVESLEEP] ?: 0
    }

    // Updates subscription status.
    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] = subscribedToSleepData
        }
    }

    suspend fun updateTotalOnReceiveSleep(totalOnReceiveSleep: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTALONRECEIVESLEEP] = totalOnReceiveSleep
        }
    }
}
