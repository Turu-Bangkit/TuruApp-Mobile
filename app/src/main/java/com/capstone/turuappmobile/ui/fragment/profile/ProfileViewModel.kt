package com.capstone.turuappmobile.ui.fragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.UserPointsResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class ProfileViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _checkPointResult = MutableLiveData<Result<UserPointsResponse>>()
    val checkPointResult: MutableLiveData<Result<UserPointsResponse>> = _checkPointResult

    fun checkPoints(token: String , id : String) = viewModelScope.launch {
        callCheckPoints(token, id)
    }

    private suspend fun callCheckPoints(token: String, id : String) {
        try {
            _checkPointResult.postValue(Result.Loading)
            val response = repository.getPoint(token, id)
            _checkPointResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _checkPointResult.postValue(Result.Error(e.message.toString()))
        }
    }
}