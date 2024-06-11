package com.bangkit.fraudguard.data.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse (
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)