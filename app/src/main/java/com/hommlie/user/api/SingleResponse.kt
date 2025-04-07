package com.hommlie.user.api

import com.google.gson.annotations.SerializedName

data class SingleResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("balance")
	val balance: Float? = null
)
