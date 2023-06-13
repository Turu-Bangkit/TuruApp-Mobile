package com.capstone.turuappmobile.di

import android.content.Context
import androidx.datastore.preferences.createDataStore
import com.capstone.turuappmobile.data.api.config.ApiConfig
import com.capstone.turuappmobile.data.datastore.SLEEP_PREFERENCES_NAME
import com.capstone.turuappmobile.data.datastore.SleepSubscriptionStatus
import com.capstone.turuappmobile.data.datastore.USER_PREFERENCES_NAME
import com.capstone.turuappmobile.data.datastore.UserDataPreferences
import com.capstone.turuappmobile.data.db.SleepDatabase
import com.capstone.turuappmobile.data.repository.SleepRepository
import com.capstone.turuappmobile.data.repository.UsersRepository

object Injection {
    fun provideRepository(context: Context): SleepRepository {
        val database = SleepDatabase.getDatabase(context)
        val sleepTimeDao = database.sleepTimeDao()
        val sleepClassifyEventDao = database.sleepClassifyEventDao()
        val sleepQualityDao = database.sleepQualityDao()
        val sleepSubscriptionStatus = SleepSubscriptionStatus(context.createDataStore(name = SLEEP_PREFERENCES_NAME))
        return SleepRepository.getInstance(sleepClassifyEventDao, sleepTimeDao, sleepQualityDao, sleepSubscriptionStatus)
    }

    fun provideUserRepository(context: Context): UsersRepository {
        val userApi = ApiConfig.getApiService()
        val userDataPreferences = UserDataPreferences(context.createDataStore(name = USER_PREFERENCES_NAME))
        return UsersRepository.getInstance(userApi, userDataPreferences)
    }
}