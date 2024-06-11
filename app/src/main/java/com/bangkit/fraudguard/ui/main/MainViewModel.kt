package com.bangkit.fraudguard.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.bangkit.fraudguard.data.dto.request.ChangePasswordRequest
import com.bangkit.fraudguard.data.dto.request.UpdateProfileRequest
import com.bangkit.fraudguard.data.dto.response.ChangePhotoResponse
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.data.repository.SpamRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    private val spamRepository: SpamRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return spamRepository.getSession().asLiveData()
    }

    fun logout() {
       runBlocking {
              spamRepository.logout()
       }
    }

    fun saveSession(userModel: UserModel) {
        runBlocking {
            spamRepository.saveSession(userModel)
        }
    }

    fun deleteAllPredictions(): LiveData<Response<ObjectResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<ObjectResponse>>()

        spamRepository.deleteAllPrediction().enqueue(object : Callback<ObjectResponse> {
            override fun onResponse(
                call: Call<ObjectResponse>,
                response: Response<ObjectResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<ObjectResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    fun getHistory() : LiveData<Response<List<History>>> = liveData {
        val responseLiveData = MutableLiveData<Response<List<History>>>()

        spamRepository.getHistory().enqueue(object : Callback<List<History>> {
            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<List<History>>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })
        emitSource(responseLiveData)

    }



    fun showProfile(): LiveData<Response<ProfileResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<ProfileResponse>>()

        spamRepository.getProfile().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<ProfileResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    }


    fun updateProfile(objectDTO : UpdateProfileRequest): LiveData<Response<ObjectResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<ObjectResponse>>()

        spamRepository.updateProfile(objectDTO).enqueue(object : Callback<ObjectResponse> {
            override fun onResponse(
                call: Call<ObjectResponse>,
                response: Response<ObjectResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<ObjectResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    }


    fun changePassword(objectDTO : ChangePasswordRequest): LiveData<Response<ObjectResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<ObjectResponse>>()

        spamRepository.changePassword(objectDTO).enqueue(object : Callback<ObjectResponse> {
            override fun onResponse(
                call: Call<ObjectResponse>,
                response: Response<ObjectResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<ObjectResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    }

    fun updateProfilePicture(photoFile : MultipartBody.Part): LiveData<Response<ChangePhotoResponse>> = liveData {
        val responseLiveData = MutableLiveData<Response<ChangePhotoResponse>>()

        spamRepository.updateProfilePicture(photoFile).enqueue(object : Callback<ChangePhotoResponse> {
            override fun onResponse(
                call: Call<ChangePhotoResponse>,
                response: Response<ChangePhotoResponse>
            ) {
                responseLiveData.value = response
            }

            override fun onFailure(call: Call<ChangePhotoResponse>, t: Throwable) {
                // Handle failure
                val errorBody = (t.message ?: "Unknown error").toResponseBody(null)
                val errorResponse = Response.error<ChangePhotoResponse>(500, errorBody)
                responseLiveData.value = errorResponse
            }
        })

        emitSource(responseLiveData)
    }




}