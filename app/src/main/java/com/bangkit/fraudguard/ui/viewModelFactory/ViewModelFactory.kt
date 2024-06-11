package com.bangkit.fraudguard.ui.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.data.injection.Injection
import com.bangkit.fraudguard.data.repository.AuthRepository
import com.bangkit.fraudguard.data.repository.SpamRepository
import com.bangkit.fraudguard.ui.login.LoginViewModel
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private var spamRepository: SpamRepository, private val authRepository: AuthRepository
) : ViewModelProvider.NewInstanceFactory(
) {
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideSpamRepositoryInjection(context),
                Injection.provideAuthRepositoryInjection(context)
            ).also { instance = it }
        }

    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(authRepository, spamRepository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(spamRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(authRepository, spamRepository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)

        }
    }
}

