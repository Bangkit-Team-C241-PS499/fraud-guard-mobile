package com.bangkit.fraudguard.data.config

import com.bangkit.fraudguard.data.dto.response.History
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiServiceSpam {
    @GET("predictions/history")
    fun getHistory(
    ): Call<History>
}