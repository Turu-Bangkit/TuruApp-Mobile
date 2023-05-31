package com.capstone.turuappmobile.ui.activity.trackSleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.turuappmobile.data.repository.UsersRepository

class SleepActivityViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

}