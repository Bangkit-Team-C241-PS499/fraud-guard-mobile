package com.bangkit.fraudguard.data.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponse (
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("name")
    val name: String
)