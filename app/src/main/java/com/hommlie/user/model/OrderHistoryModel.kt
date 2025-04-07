package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponse(

	@field:SerializedName("data")
	val data: ArrayList<OrderHistoryDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class OrderHistoryData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<OrderHistoryDataItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: Any? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

data class OrderHistoryDataItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("payment_type")
	val paymentType: Int? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("product_id")
	val product_id: Int? = null,

	@field:SerializedName("order_total")
	val grandTotal: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("desired_time")
	val desired_time: String? = null,

	@field:SerializedName("desired_date")
	val desired_date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("order_status")
	val order_status: Int? = null,
)
