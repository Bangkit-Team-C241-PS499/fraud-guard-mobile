package com.bangkit.fraudguard.data.injection

import android.content.Context
import com.bangkit.fraudguard.data.config.getApiServiceArticle
import com.bangkit.fraudguard.data.config.getApiServiceAuth
import com.bangkit.fraudguard.data.preferences.UserPreference
import com.bangkit.fraudguard.data.preferences.dataStore
import com.bangkit.fraudguard.data.repository.ArticleRepository
import com.bangkit.fraudguard.data.repository.AuthRepository
import com.bangkit.fraudguard.data.repository.SpamRepository

object Injection {
    fun provideAuthRepositoryInjection(context: Context): AuthRepository {
        val apiServiceAuth = getApiServiceAuth()
        return AuthRepository.getInstance(apiServiceAuth)
    }

    fun provideSpamRepositoryInjection(context: Context): SpamRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return SpamRepository.getInstance(pref)
    }

    fun provideArticleRepositoryInjection(context: Context): ArticleRepository {
        val apiServiceArticle = getApiServiceArticle()
        return ArticleRepository.getInstance(apiServiceArticle)
    }
}