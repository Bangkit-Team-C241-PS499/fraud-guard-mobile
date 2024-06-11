package com.bangkit.fraudguard.data.config

import com.bangkit.fraudguard.data.dto.request.ChangePasswordRequest
import com.bangkit.fraudguard.data.dto.request.UpdateProfileRequest
import com.bangkit.fraudguard.data.dto.response.ChangePhotoResponse
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.HistoryResponse
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiServiceSpam {

    @GET("user/profile")
    fun getProfile(): Call<ProfileResponse>

    @PUT("user/profile")
    fun updateProfile(@Body dto: UpdateProfileRequest): Call<ObjectResponse>

    @PUT("user/change-password")
    fun changePassword(@Body dto: ChangePasswordRequest): Call<ObjectResponse>

    @POST("user/profile/picture")
    fun updateProfilePicture(@Part file: MultipartBody.Part): Call<ChangePhotoResponse>

    @GET("predictions/history")
    fun getHistory(): Call<List<History>>


}