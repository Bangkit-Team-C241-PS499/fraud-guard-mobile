package com.bangkit.fraudguard.data.dto.response

data class HistoryResponse(
	val response: List<History>
)

data class History(
	val createdAt: String? = null,
	val deletedAt: String? = null,
	val userId: Int? = null,
	val id: String? = null,
	val label: String? = null,
	val message: String? = null,
	val prediction: Float? = null,
	val updatedAt: String? = null,
)

