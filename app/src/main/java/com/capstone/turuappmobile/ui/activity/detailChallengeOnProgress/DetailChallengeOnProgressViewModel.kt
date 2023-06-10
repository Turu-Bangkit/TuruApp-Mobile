package com.capstone.turuappmobile.ui.activity.detailChallengeOnProgress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.DetailChallengeResponse
import com.capstone.turuappmobile.data.api.model.StatusChallengeResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class DetailChallengeOnProgressViewModel (private val repository: UsersRepository)  : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _detailChallengeResult = MutableLiveData<Result<DetailChallengeResponse>>()
    val detailChallengeResult: MutableLiveData<Result<DetailChallengeResponse>> = _detailChallengeResult

    private val _checkStatusChallenge = MutableLiveData<Result<StatusChallengeResponse>>()
    val checkStatusChallenge: MutableLiveData<Result<StatusChallengeResponse>> = _checkStatusChallenge

    fun detailChallenge(token: String, idChallenge: String) = viewModelScope.launch {
        callDetailChallenge(token, idChallenge)
    }

    private suspend fun callDetailChallenge(token: String, idChallenge: String){
        try {
            _detailChallengeResult.postValue(Result.Loading)
            val response = repository.getDetailChallenge(token, idChallenge)
            _detailChallengeResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _detailChallengeResult.postValue(Result.Error(e.message.toString()))
        }
    }

    fun statusChallenge(token: String , id : String) = viewModelScope.launch {
        callStatusChallenge(token, id)
    }

    private suspend fun callStatusChallenge( token: String, id : String) {
        try {
            _checkStatusChallenge.postValue(Result.Loading)
            val response = repository.getStatusChallenge(token, id)
            _checkStatusChallenge.postValue(Result.Success(response))
        } catch (e: Exception) {
            _checkStatusChallenge.postValue(Result.Error(e.message.toString()))
        }
    }



}