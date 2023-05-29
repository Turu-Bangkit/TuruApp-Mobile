package com.capstone.turuappmobile.data.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.turuappmobile.data.repository.UsersRepository
import com.capstone.turuappmobile.di.Injection
import com.capstone.turuappmobile.ui.activity.login.LoginViewModel


class ViewModelFactoryUser(
    private val usersRepository: UsersRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryUser? = null
        fun getInstance(): ViewModelFactoryUser =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryUser(Injection.provideUserRepository())
            }.also { instance = it }
    }

}