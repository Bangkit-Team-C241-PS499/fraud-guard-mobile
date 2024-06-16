package com.bangkit.fraudguard.data.dto.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("UserId")
	val userId: Int? = null,

	@field:SerializedName("prediction")
	val prediction: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
