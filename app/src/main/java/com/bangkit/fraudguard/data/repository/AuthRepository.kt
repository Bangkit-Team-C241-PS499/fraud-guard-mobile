package com.bangkit.fraudguard.data.repository

import com.bangkit.fraudguard.data.config.ApiServiceAuth
import com.bangkit.fraudguard.data.dto.request.LoginRequest
import com.bangkit.fraudguard.data.dto.request.RegisterRequest
import com.bangkit.fraudguard.data.dto.response.AuthResponse
import retrofit2.Call

class AuthRepository private constructor(
    private val apiServiceAuth: ApiServiceAuth

) {

    fun login(loginRequest: LoginRequest): Call<AuthResponse> {
        return apiServiceAuth.login(loginRequest)
    }

    fun register(registerRequest: RegisterRequest): Call<AuthResponse> {
        return apiServiceAuth.register(registerRequest)
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(apiServiceAuth: ApiServiceAuth): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiServiceAuth).also { instance = it }
            }
    }

}