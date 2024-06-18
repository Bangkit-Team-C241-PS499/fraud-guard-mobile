package com.bangkit.fraudguard.data.dto.request

import com.google.gson.annotations.SerializedName

data class PredictRequest(
    @field:SerializedName("message")
    val message: String,
)
