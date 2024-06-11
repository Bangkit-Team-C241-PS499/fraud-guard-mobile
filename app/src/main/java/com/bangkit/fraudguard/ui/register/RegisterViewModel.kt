package com.bangkit.fraudguard.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bangkit.fraudguard.data.dto.request.RegisterRequest
import com.bangkit.fraudguard.data.dto.response.AuthResponse
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.data.repository.AuthRepository
import com.bangkit.fraudguard.data.repository.SpamRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val spamRepository: SpamRepository
) : ViewModel() {

    fun getSession() {
        viewModelScope.launch {
            spamRepository.getSession()
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            spamRepository.saveSession(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            spamRepository.logout()
        }
    }
    fun register(registerRequest: RegisterRequest ): LiveData<Response<AuthResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<AuthResponse>>()

        authRepository.register(registerRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<AuthResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    }

}