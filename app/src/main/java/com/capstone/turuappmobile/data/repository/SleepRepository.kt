package com.capstone.turuappmobile.data.repository

import com.capstone.turuappmobile.data.datastore.SleepSubscriptionStatus
import com.capstone.turuappmobile.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SleepRepository(
    private val sleepClassifyEventDao: SleepClassifyEventDao,
    private val sleepTimeDao: SleepTimeDao,
    private val sleepQualityDao: SleepQualityDao,
    private val sleepSubscriptionStatus: SleepSubscriptionStatus
) {

    val subscribedToSleepDataFlow: Flow<Boolean> = sleepSubscriptionStatus.subscribedToSleepDataFlow
    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) =
        sleepSubscriptionStatus.updateSubscribedToSleepData(subscribedToSleepData)

    val totalOnReceiveSleep: Flow<Int> = sleepSubscriptionStatus.totalOnReceiveSleep

    suspend fun updateTotalOnReceiveSleep(totalOnReceiveSleep: Int) =
        sleepSubscriptionStatus.updateTotalOnReceiveSleep(totalOnReceiveSleep)


    val allSleepClassifyEvents: Flow<List<SleepClassifyEventEntity>> =
        sleepClassifyEventDao.getAll()

    suspend fun insertSleepClassifyEvent(sleepClassifyEventEntity: SleepClassifyEventEntity) {
        sleepClassifyEventDao.insert(sleepClassifyEventEntity)
    }

    suspend fun insertSleepClassifyEvents(sleepClassifyEventEntities: List<SleepClassifyEventEntity>) {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntities)
    }

    val allSleepTime: Flow<List<SleepTimeEntity>> = sleepTimeDao.getAll()

    fun allSleepTimeByUser(userUID: String, query: String): Flow<List<SleepTimeEntity>> =
        sleepTimeDao.getById(userUID).map {
            it.filter { sleepTimeEntity ->
                sleepTimeEntity.startTime.toString().contains(query, ignoreCase = true)
            }
        }

    fun sleepTimeByRangeDate(userUID: String, startDate : Int , endDate : Int): Flow<List<SleepTimeEntity>> =
        sleepTimeDao.getByRangeDate(userUID, startDate, endDate)


    fun allSleepTimeByUserLimit(userUID: String): Flow<List<SleepTimeEntity>> =
        sleepTimeDao.getByIdLimit(userUID)

    suspend fun insertSleepTime(sleepTimeEntity: SleepTimeEntity) {
        sleepTimeDao.insert(sleepTimeEntity)
    }

    fun allByEpoch(startTime: Int, endTime: Int): Flow<List<SleepClassifyEventEntity>> =
        sleepClassifyEventDao.getByEpoch(startTime, endTime)


    suspend fun updateEndTime(endTime: Int) {
        sleepTimeDao.update(endTime)
    }

    suspend fun updateRealStartTime(realStartTime: Int) {
        sleepTimeDao.updateRealStartTime(realStartTime)
    }

    suspend fun insertSleepQuality(sleepQualityEntity: SleepQualityEntity) {
        sleepQualityDao.insert(sleepQualityEntity)
    }
    fun allSleepQualityLimit(userUID: String): Flow<List<SleepQualityEntity>> =
        sleepQualityDao.getQualityLimit(userUID)

    companion object {
        @Volatile
        private var instance: SleepRepository? = null

        fun getInstance(
            sleepClassifyEventDao: SleepClassifyEventDao,
            sleepTimeDao: SleepTimeDao,
            sleepQualityDao: SleepQualityDao,
            sleepSubscriptionStatus: SleepSubscriptionStatus
        ): SleepRepository {
            return instance ?: synchronized(this) {
                instance ?: SleepRepository(
                    sleepClassifyEventDao,
                    sleepTimeDao,
                    sleepQualityDao,
                    sleepSubscriptionStatus
                ).also { instance = it }
            }
        }
    }
}