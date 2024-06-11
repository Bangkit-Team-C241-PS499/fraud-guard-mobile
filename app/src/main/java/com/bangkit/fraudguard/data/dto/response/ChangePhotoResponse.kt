package com.bangkit.fraudguard.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChangePhotoResponse (
    @field:SerializedName("url")
    val url: String
)