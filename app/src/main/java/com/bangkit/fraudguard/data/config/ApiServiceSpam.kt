package com.bangkit.fraudguard.data.config

import com.bangkit.fraudguard.data.dto.request.ChangePasswordRequest
import com.bangkit.fraudguard.data.dto.request.PredictRequest
import com.bangkit.fraudguard.data.dto.request.UpdateProfileRequest
import com.bangkit.fraudguard.data.dto.response.ChangePhotoResponse
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.HistoryResponse
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.data.dto.response.PredictResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiServiceSpam {

    @GET("user/profile")
    fun getProfile(): Call<ProfileResponse>

    @PUT("user/profile")
    fun updateProfile(@Body dto: UpdateProfileRequest): Call<ObjectResponse>

    @PUT("user/change-password")
    fun changePassword(@Body dto: ChangePasswordRequest): Call<ObjectResponse>

    @Multipart
    @POST("user/profile/picture")
    fun updateProfilePicture(@Part file: MultipartBody.Part): Call<ChangePhotoResponse>

    @DELETE("predictions/")
    fun deleteAllPredictions(): Call<ObjectResponse>
  
    @GET("predictions/history")
    fun getHistory(): Call<List<History>>

    @GET("predictions/{id}")
    fun getDetailHistory(@Path("id") id: String):Call<History>

    @POST("predictions")
    fun predict(@Body dto:PredictRequest): Call<PredictResponse>

}