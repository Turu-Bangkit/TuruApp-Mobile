package com.capstone.turuappmobile.ui.activity.trackSleep

import androidx.lifecycle.*
import com.capstone.turuappmobile.data.repository.SleepRepository
import com.capstone.turuappmobile.data.db.SleepClassifyEventEntity
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

    fun insertStartTimeSleep(timeSleep : SleepTimeEntity) = viewModelScope.launch {
        repository.insertSleepTime(timeSleep)
    }

    fun updateEndTimeSleep(endTime : Int) = viewModelScope.launch {
        repository.updateEndTime(endTime)
    }

    val allSleepHistory: LiveData<List<SleepTimeEntity>> = repository.allSleepTime.asLiveData()

    fun allByEpoch(startTime: Int, endTime: Int): LiveData<List<SleepClassifyEventEntity>> =
        repository.allByEpoch(startTime, endTime).asLiveData()



}
