package com.capstone.turuappmobile.data.repository

import com.capstone.turuappmobile.data.api.config.ApiService
import com.capstone.turuappmobile.data.datastore.SleepSubscriptionStatus
import com.capstone.turuappmobile.data.db.SleepClassifyEventDao
import com.capstone.turuappmobile.data.db.SleepTimeDao

class UsersRepository(
    private val userApi: ApiService
) {

    suspend fun loginMember(tokenfirebase: String) = userApi.login(tokenfirebase)

    suspend fun getPoint(id: String) = userApi.getPoint(id)

    suspend fun addPoint(token: String, id: String, point: String) =
        userApi.addPoint(token, id, point)

    suspend fun sendSleepTime(token: String, id: String, sleepTime: String, endTime: String) =
        userApi.sendSleepTime(token, id, sleepTime, endTime)

    suspend fun getAllChallenge(token: String) = userApi.getAllChallenge(token)

    companion object {
        @Volatile
        private var instance: UsersRepository? = null

        fun getInstance(
            userApi: ApiService
        ): UsersRepository {
            return instance ?: synchronized(this) {
                instance ?: UsersRepository(
                    userApi1
                ).also { instance = it }
            }
        }
    }

}