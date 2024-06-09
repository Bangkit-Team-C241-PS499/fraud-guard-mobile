package com.bangkit.fraudguard.data.repository


import com.bangkit.fraudguard.data.config.ApiService
import com.bangkit.fraudguard.data.config.getApiService
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.data.preferences.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SpamRepository private constructor(
    private val userPreference: UserPreference,
) {
    private lateinit var apiService: ApiService

    fun getSession(): Flow<UserModel> {
        var session = userPreference.getSession()
        runBlocking {
            apiService = getApiService(session.first().token)
        }
        return session
    }

    suspend fun saveSession(user: UserModel) {
        runBlocking {
            userPreference.saveSession(user)
        }
        apiService = getApiService(user.token)

    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: SpamRepository? = null
        fun getInstance(
            userPreference: UserPreference,
        ): SpamRepository =
            instance ?: synchronized(this) {
                instance ?: SpamRepository(userPreference)
            }.also { instance = it }
    }
}