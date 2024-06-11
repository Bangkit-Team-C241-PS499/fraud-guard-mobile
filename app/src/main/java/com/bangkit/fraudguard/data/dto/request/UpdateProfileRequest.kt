package com.bangkit.fraudguard.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest (

    @field:SerializedName("name")
    val name: String
)