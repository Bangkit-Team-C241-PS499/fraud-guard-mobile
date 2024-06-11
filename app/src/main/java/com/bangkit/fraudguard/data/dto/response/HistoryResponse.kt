package com.bangkit.fraudguard.data.dto.response

import com.google.gson.annotations.SerializedName

data class History(

	@field:SerializedName("Response")
	val response: List<HistoryItem?>? = null
)

data class HistoryItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("UserId")
	val userId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
