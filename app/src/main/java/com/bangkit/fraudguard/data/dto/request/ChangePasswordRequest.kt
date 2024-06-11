package com.bangkit.fraudguard.data.dto.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest (
    @field:SerializedName("oldPassword")
    val oldPassword: String,
    @field:SerializedName("newPassword")
    val newPassword: String,
    @field:SerializedName("confirmNewPassword")
    val confirmNewPassword: String
)