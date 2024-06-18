package com.bangkit.fraudguard.data.repository

import com.bangkit.fraudguard.BuildConfig
import com.bangkit.fraudguard.data.config.ApiServiceArticle
import com.bangkit.fraudguard.data.dto.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.Query

class ArticleRepository private constructor(
    private val apiServiceArticle: ApiServiceArticle
) {

    fun getArticle(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String = BuildConfig.ARTICLE_KEY
    ): Call<ArticleResponse> {
        return apiServiceArticle.getArticle(query, language, sortBy, apiKey)
    }

    companion object {
        @Volatile
        private var instance: ArticleRepository? = null
        fun getInstance(
            apiServiceArticle: ApiServiceArticle
        ): ArticleRepository =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(apiServiceArticle)
            }.also { instance = it }
    }
}