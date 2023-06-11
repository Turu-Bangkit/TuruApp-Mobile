package com.capstone.turuappmobile.ui.activity.detailCatalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.BasicResponse
import com.capstone.turuappmobile.data.api.model.DetailCatalogResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class DetailCatalogViewModel(private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _detailCatalogResult = MutableLiveData<Result<DetailCatalogResponse>>()
    val detailCatalogResult: MutableLiveData<Result<DetailCatalogResponse>> = _detailCatalogResult

    private val _exchangePointResult = MutableLiveData<Result<BasicResponse>>()
    val exchangePointResult: MutableLiveData<Result<BasicResponse>> = _exchangePointResult


    fun detailCatalog(token: String, idCatalog: String) = viewModelScope.launch {
        callDetailCatalog(token, idCatalog)
    }

    private suspend fun callDetailCatalog(token: String, idCatalog: String) {
        try {
            _detailCatalogResult.postValue(Result.Loading)
            val response = repository.getDetailCatalog(token, idCatalog)
            _detailCatalogResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _detailCatalogResult.postValue(Result.Error(e.message.toString()))
        }
    }

    fun exchangePoint(token: String, UIDUser: String, idCatalog: String) = viewModelScope.launch {
        callExchangePoint(token, UIDUser, idCatalog)
    }

    private suspend fun callExchangePoint(token: String, uidUser: String, idCatalog: String) {
        try {
            _exchangePointResult.postValue(Result.Loading)
            val response = repository.exchangePoint(token, uidUser, idCatalog)
            _exchangePointResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _exchangePointResult.postValue(Result.Error(e.message.toString()))
        }

    }


}