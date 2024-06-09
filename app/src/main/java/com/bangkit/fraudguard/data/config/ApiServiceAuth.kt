package com.bangkit.fraudguard.data.config

import com.bangkit.fraudguard.data.dto.request.LoginRequest
import com.bangkit.fraudguard.data.dto.request.RegisterRequest
import com.bangkit.fraudguard.data.dto.response.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceAuth {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun register(@Body signUpRequest: RegisterRequest): Call<AuthResponse>
}