package com.capstone.turuappmobile.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.AllCatalogRespone
import com.capstone.turuappmobile.data.api.model.BasicResponse
import com.capstone.turuappmobile.data.api.model.StatusChallengeResponse
import com.capstone.turuappmobile.data.api.model.UserPointsResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel (private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _checkPointResult = MutableLiveData<Result<UserPointsResponse>>()
    val checkPointResult: MutableLiveData<Result<UserPointsResponse>> = _checkPointResult

    private val _checkStatusChallenge = MutableLiveData<Result<StatusChallengeResponse>>()
    val checkStatusChallenge: MutableLiveData<Result<StatusChallengeResponse>> = _checkStatusChallenge

    private val _updateLevelResult = MutableLiveData<Result<BasicResponse>>()
    val updateLevelResult: MutableLiveData<Result<BasicResponse>> = _updateLevelResult

    private val _catalogResult = MutableLiveData<Result<AllCatalogRespone>>()
    val catalogResult: MutableLiveData<Result<AllCatalogRespone>> = _catalogResult

    private val alreadyCall = MutableLiveData<Boolean>()

    fun getAlreadyCall() : Boolean{
        return alreadyCall.value ?: false
    }

    fun alreadyCall(){
        alreadyCall.value = true
    }



    fun allcatalog(token: String) = viewModelScope.launch {
        callAllcatalog(token)
    }

    private suspend fun callAllcatalog(token: String){

        try {
            if(_catalogResult.value is Result.Success){
                return
            }else{
                _catalogResult.postValue(Result.Loading)
                val response = repository.getAllCatalog(token)
                _catalogResult.postValue(Result.Success(response))
            }
        } catch (e: Exception) {
            _catalogResult.postValue(Result.Error(e.message.toString()))
        }

    }

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

    fun updateLevel(token: String , id : String, level: Int) = viewModelScope.launch {
        callUpdateLevel(token, id, level)
    }

    private suspend fun callUpdateLevel(token: String, id : String, level: Int) {
        try {
            _updateLevelResult.postValue(Result.Loading)
            val response = repository.updateLevelChallenge(token, id, level)
            _updateLevelResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _updateLevelResult.postValue(Result.Error(e.message.toString()))
        }
    }
}