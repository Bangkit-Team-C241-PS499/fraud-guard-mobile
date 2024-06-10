package com.bangkit.fraudguard.data.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("email") val email: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("password") val password: String,
    @field:SerializedName("confirmPassword") val confirmPassword: String
)
