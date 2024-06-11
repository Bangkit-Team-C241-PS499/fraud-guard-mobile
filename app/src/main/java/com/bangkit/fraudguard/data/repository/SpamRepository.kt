package com.bangkit.fraudguard.data.repository


import com.bangkit.fraudguard.data.config.ApiServiceSpam
import com.bangkit.fraudguard.data.config.getApiServiceSpam
import com.bangkit.fraudguard.data.dto.request.ChangePasswordRequest
import com.bangkit.fraudguard.data.dto.request.UpdateProfileRequest
import com.bangkit.fraudguard.data.dto.response.ChangePhotoResponse
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.data.preferences.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import retrofit2.Call

class SpamRepository private constructor(
    private val userPreference: UserPreference,
) {
    private lateinit var apiServiceSpam: ApiServiceSpam

    fun getSession(): Flow<UserModel> {
        var session = userPreference.getSession()
        runBlocking {
            apiServiceSpam = getApiServiceSpam(session.first().token)
        }
        return session
    }

    suspend fun saveSession(user: UserModel) {
        runBlocking {
            userPreference.saveSession(user)
        }
        apiServiceSpam = getApiServiceSpam(user.token)

    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getProfile() : Call<ProfileResponse>{
        return apiServiceSpam.getProfile()

    }

    fun getHistory(): Call<List<History>>{
        return apiServiceSpam.getHistory()
    }

    fun historyDetail(id:String): Call<History>{
        return apiServiceSpam.getDetailHistory(id)
    }

    fun updateProfile(objectDTO : UpdateProfileRequest) : Call<ObjectResponse>{
        return apiServiceSpam.updateProfile(objectDTO)

    }

    fun changePassword(objectDTO : ChangePasswordRequest) : Call<ObjectResponse>{
        return apiServiceSpam.changePassword(objectDTO)

    }

    fun updateProfilePicture(photoFile : MultipartBody.Part) : Call<ChangePhotoResponse>{
        return apiServiceSpam.updateProfilePicture(photoFile)

    }

    fun deleteAllPrediction() : Call<ObjectResponse>{
        return apiServiceSpam.deleteAllPredictions()

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