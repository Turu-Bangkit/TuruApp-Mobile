package com.capstone.turuappmobile.ui.fragment.historyList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.turuappmobile.data.repository.UsersRepository

class HistorySleepListViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

}