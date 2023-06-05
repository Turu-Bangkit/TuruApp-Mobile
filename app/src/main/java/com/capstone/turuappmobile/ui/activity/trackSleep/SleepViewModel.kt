package com.capstone.turuappmobile.ui.activity.trackSleep

import androidx.lifecycle.*
import com.capstone.turuappmobile.data.repository.SleepRepository
import com.capstone.turuappmobile.data.db.SleepClassifyEventEntity
import com.capstone.turuappmobile.data.db.SleepQualityEntity
import com.capstone.turuappmobile.data.db.SleepSumEntity
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SleepViewModel (private val repository: SleepRepository) : ViewModel() {

    val subscribedToSleepDataLiveData = repository.subscribedToSleepDataFlow.asLiveData()

    fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        repository.updateSubscribedToSleepData(subscribed)
    }

    val allSleepClassifyEventEntities: LiveData<List<SleepClassifyEventEntity>> =
        repository.allSleepClassifyEvents.asLiveData()

    fun updateTotalOnReceiveSleep(totalOnReceiveSleep: Int) = viewModelScope.launch {
        repository.updateTotalOnReceiveSleep(totalOnReceiveSleep)
    }

    fun insertStartTimeSleep(timeSleep : SleepTimeEntity) = viewModelScope.launch {
        repository.insertSleepTime(timeSleep)
    }

    fun updateEndTimeSleep(endTime : Int) = viewModelScope.launch {
        repository.updateEndTime(endTime)
    }

    val allSleepHistory: LiveData<List<SleepTimeEntity>> = repository.allSleepTime.asLiveData()

    fun allSleepHistoryByUser(userUID: String, query: String): LiveData<List<SleepTimeEntity>> =
        repository.allSleepTimeByUser(userUID, query).asLiveData()

    fun allSleepHistoryByUserLimit(userUID: String): LiveData<List<SleepTimeEntity>> =
        repository.allSleepTimeByUserLimit(userUID).asLiveData()

    fun allSleepHistoryByRangeDate(userUID: String, startDate : Int , endDate : Int): LiveData<List<SleepTimeEntity>> =
        repository.sleepTimeByRangeDate(userUID, startDate, endDate).asLiveData()

    fun allByEpoch(startTime: Int, endTime: Int): LiveData<List<SleepClassifyEventEntity>> =
        repository.allByEpoch(startTime, endTime).asLiveData()

    fun allSleepQuality(userUID: String): LiveData<List<SleepQualityEntity>> =
        repository.allSleepQualityLimit(userUID).asLiveData()

    fun insertSleepQuality (sleepQualityEntity: SleepQualityEntity) = viewModelScope.launch {
        repository.insertSleepQuality(sleepQualityEntity)
    }



}
