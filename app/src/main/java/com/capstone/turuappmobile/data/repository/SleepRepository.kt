package com.capstone.turuappmobile.data.repository

import com.capstone.turuappmobile.data.datastore.SleepSubscriptionStatus
import com.capstone.turuappmobile.data.db.SleepClassifyEventDao
import com.capstone.turuappmobile.data.db.SleepClassifyEventEntity
import com.capstone.turuappmobile.data.db.SleepTimeDao
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepClassifyEventDao: SleepClassifyEventDao,
    private val sleepTimeDao: SleepTimeDao,
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

    fun allSleepTimeByUser(userUID: String): Flow<List<SleepTimeEntity>> =
        sleepTimeDao.getById(userUID)

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

    companion object {
        @Volatile
        private var instance: SleepRepository? = null

        fun getInstance(
            sleepClassifyEventDao: SleepClassifyEventDao,
            sleepTimeDao: SleepTimeDao,
            sleepSubscriptionStatus: SleepSubscriptionStatus
        ): SleepRepository {
            return instance ?: synchronized(this) {
                instance ?: SleepRepository(
                    sleepClassifyEventDao,
                    sleepTimeDao,
                    sleepSubscriptionStatus
                ).also { instance = it }
            }
        }
    }
}