package com.bangkit.fraudguard.data.config

import com.bangkit.fraudguard.BuildConfig
import com.bangkit.fraudguard.data.dto.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServiceArticle {
    @GET("everything")
    fun getArticle(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String = BuildConfig.ARTICLE_KEY
    ): Call<ArticleResponse>
}
