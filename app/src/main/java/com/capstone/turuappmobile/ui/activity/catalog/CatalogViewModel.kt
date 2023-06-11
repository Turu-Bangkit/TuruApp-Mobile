package com.capstone.turuappmobile.ui.activity.catalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.turuappmobile.data.api.model.AllCatalogRespone
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.repository.UsersRepository
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: UsersRepository) : ViewModel() {

    val getUserSession = repository.getUserSession.asLiveData()

    private val _catalogResult = MutableLiveData<Result<AllCatalogRespone>>()
    val catalogResult: MutableLiveData<Result<AllCatalogRespone>> = _catalogResult

    fun allcatalog(token: String) = viewModelScope.launch {
        callAllcatalog(token)
    }

    private suspend fun callAllcatalog(token: String){

        try {
            _catalogResult.postValue(Result.Loading)
            val response = repository.getAllCatalog(token)
            _catalogResult.postValue(Result.Success(response))
        } catch (e: Exception) {
            _catalogResult.postValue(Result.Error(e.message.toString()))
        }

    }



}