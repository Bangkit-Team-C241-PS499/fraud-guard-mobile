package com.bangkit.fraudguard.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.data.repository.SpamRepository

class MainViewModel(
    private val spamRepository: SpamRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return spamRepository.getSession().asLiveData()
    }


}