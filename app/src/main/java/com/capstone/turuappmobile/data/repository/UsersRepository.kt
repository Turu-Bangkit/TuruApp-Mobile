package com.capstone.turuappmobile.data.repository

import com.capstone.turuappmobile.data.api.config.ApiService
import com.capstone.turuappmobile.data.api.model.UserPreferencesModel
import com.capstone.turuappmobile.data.datastore.SleepSubscriptionStatus
import com.capstone.turuappmobile.data.datastore.UserDataPreferences
import kotlinx.coroutines.flow.Flow


class UsersRepository(
    private val userApi: ApiService,
    private val userDataPreferences: UserDataPreferences
) {

    val getUserSession: Flow<UserPreferencesModel> = userDataPreferences.getSession()
    suspend fun updateUserSession(session: UserPreferencesModel) =
        userDataPreferences.saveSession(session)


    suspend fun checkToken(tokenfirebase: String) = userApi.login(tokenfirebase)

    suspend fun getPoint(token: String, id: String) = userApi.getPoint(token, id)

    suspend fun addPoint(token: String, id: String, point: String) =
        userApi.addPoint(token, id, point)

    suspend fun sendSleepTime(token: String, id: String, sleepTime: String, endTime: String) =
        userApi.sendSleepTime(token, id, sleepTime, endTime)

    suspend fun getAllChallenge(token: String) = userApi.getAllChallenge(token)

    suspend fun getDetailChallenge(token: String, idChallenge: String) =
        userApi.getDetailChallenge(token, idChallenge)

    suspend fun startChallenge(token: String, UIDUser: String, idChallenge: String) =
        userApi.startChallenge(token, UIDUser, idChallenge)

    suspend fun getStatusChallenge(token: String, UIDUser: String) =
        userApi.getStatusChallenge(token, UIDUser)

    suspend fun updateLevelChallenge(token: String, UIDUser: String, level: Int) =
        userApi.updateLevelChallenge(token, UIDUser, level)

    suspend fun getAllCatalog(token: String) = userApi.getAllCatalog(token)

    suspend fun getDetailCatalog(token: String, idCatalog: String) = userApi.getDetailCatalog(token, idCatalog)

    suspend fun exchangePoint(token: String, idUser: String, idCatalog: String ) = userApi.exchangePoint(token, idUser, idCatalog)

    companion object {
        @Volatile
        private var instance: UsersRepository? = null

        fun getInstance(
            userApi: ApiService,
            userDataPreferences: UserDataPreferences
        ): UsersRepository {
            return instance ?: synchronized(this) {
                instance ?: UsersRepository(
                    userApi,
                    userDataPreferences
                ).also { instance = it }
            }
        }
    }

}