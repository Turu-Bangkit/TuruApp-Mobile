package com.capstone.turuappmobile.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capstone.turuappmobile.data.api.model.UserPreferencesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val USER_PREFERENCES_NAME = "user_preferences"


class UserDataPreferences (private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<UserPreferencesModel> {
        return dataStore.data.map { preferences ->
            UserPreferencesModel(
                preferences[UID] ?: "",
                preferences[TOKEN] ?: ""
            )
        }
    }

    suspend fun saveSession(session: UserPreferencesModel) {
        dataStore.edit { preferences ->
            preferences[UID] = session.UID
            preferences[TOKEN] = session.jwtToken
        }
    }



    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserDataPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val TOKEN = stringPreferencesKey("jwtToken")
        private val UID = stringPreferencesKey("userUID")

    }

}