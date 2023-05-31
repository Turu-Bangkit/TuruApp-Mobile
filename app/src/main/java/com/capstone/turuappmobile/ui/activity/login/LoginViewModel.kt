package com.capstone.turuappmobile.ui.activity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.LoginResponse
import com.capstone.turuappmobile.data.api.model.UserPreferencesModel
import com.capstone.turuappmobile.data.repository.UsersRepository
import com.capstone.turuappmobile.data.repository.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _checkTokenResult = MutableLiveData<Result<LoginResponse>>()
    val checkTokenResult: MutableLiveData<Result<LoginResponse>> = _checkTokenResult

    fun updateUserSession(session: UserPreferencesModel) = viewModelScope.launch {
        usersRepository.updateUserSession(session)
    }


    fun checkToken(token: String) = viewModelScope.launch {
        callCheckToken(token)
    }

    private suspend fun callCheckToken(token: String) {
        try {
            _checkTokenResult.postValue(Result.Loading)
            val response = usersRepository.checkToken(token)
            _checkTokenResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _checkTokenResult.postValue(Result.Error(e.message.toString()))
        }
    }

}