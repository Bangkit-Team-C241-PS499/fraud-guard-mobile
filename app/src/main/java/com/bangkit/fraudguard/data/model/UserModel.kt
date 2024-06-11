package com.bangkit.fraudguard.data.model

data class UserModel(
    val email: String,
    var name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
)
