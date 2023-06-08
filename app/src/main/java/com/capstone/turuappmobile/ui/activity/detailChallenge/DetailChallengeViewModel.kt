package com.capstone.turuappmobile.ui.activity.detailChallenge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.BasicResponse
import com.capstone.turuappmobile.data.api.model.DetailChallengeResponse
import com.capstone.turuappmobile.data.api.model.UserPointsResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class DetailChallengeViewModel(private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _detailChallengeResult = MutableLiveData<Result<DetailChallengeResponse>>()
    val detailChallengeResult: MutableLiveData<Result<DetailChallengeResponse>> = _detailChallengeResult

    private val _startChallengeResult = MutableLiveData<Result<BasicResponse>>()
    val startChallengeResult: MutableLiveData<Result<BasicResponse>> = _startChallengeResult

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


    fun startChallenge(token: String, UIDUser: String, idChallenge: String) =
        viewModelScope.launch {
            callStartChallenge(token, UIDUser, idChallenge)
        }

    private suspend fun callStartChallenge(token: String, UIDUser: String, idChallenge: String) {
        try {
            _startChallengeResult.postValue(Result.Loading)
            val response = repository.startChallenge(token, UIDUser, idChallenge)
            _startChallengeResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _startChallengeResult.postValue(Result.Error(e.message.toString()))
        }
    }

}