package com.bangkit.fraudguard.data.dto.response

import java.util.Date

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
	val timestamp: String? = null,
	val updatedAt: String? = null,
)

