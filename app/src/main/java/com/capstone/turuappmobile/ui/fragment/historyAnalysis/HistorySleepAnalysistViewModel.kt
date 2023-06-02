package com.capstone.turuappmobile.ui.fragment.historyAnalysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.turuappmobile.data.repository.UsersRepository

class HistorySleepAnalysistViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

}