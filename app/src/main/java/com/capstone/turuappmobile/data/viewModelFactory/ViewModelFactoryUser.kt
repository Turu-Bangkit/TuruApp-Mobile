package com.capstone.turuappmobile.data.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.turuappmobile.data.repository.UsersRepository
import com.capstone.turuappmobile.di.Injection
import com.capstone.turuappmobile.ui.activity.login.LoginViewModel
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepActivityViewModel
import com.capstone.turuappmobile.ui.fragment.historyAnalysis.HistorySleepAnalysistViewModel
import com.capstone.turuappmobile.ui.fragment.historyList.HistorySleepListViewModel
import com.capstone.turuappmobile.ui.fragment.home.HomeFragmentViewModel


class ViewModelFactoryUser(
    private val usersRepository: UsersRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
            return HomeFragmentViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(SleepActivityViewModel::class.java)) {
            return SleepActivityViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(HistorySleepListViewModel::class.java)) {
            return HistorySleepListViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(HistorySleepAnalysistViewModel::class.java)) {
            return HistorySleepAnalysistViewModel(usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryUser? = null
        fun getInstance(context: Context): ViewModelFactoryUser =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryUser(Injection.provideUserRepository(context))
            }.also { instance = it }
    }

}