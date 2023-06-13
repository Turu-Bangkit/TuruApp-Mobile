package com.capstone.turuappmobile.ui.fragment.challenge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.AllChallengeResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class ChallengeFragmentViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _challengeResult = MutableLiveData<Result<AllChallengeResponse>>()
    val challengeResult: MutableLiveData<Result<AllChallengeResponse>> = _challengeResult

    private val alreadyCall = MutableLiveData<Boolean>()

    fun alreadyCall(){
        alreadyCall.value = true
    }

    fun getAlreadyCall() : Boolean{
        return alreadyCall.value ?: false
    }

    fun allChallenge(token: String) = viewModelScope.launch {
        callAllChallenge(token)
    }

    private suspend fun callAllChallenge(token: String){

        try {
            _challengeResult.postValue(Result.Loading)
            val response = repository.getAllChallenge(token)
            _challengeResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _challengeResult.postValue(Result.Error(e.message.toString()))
        }

    }



}