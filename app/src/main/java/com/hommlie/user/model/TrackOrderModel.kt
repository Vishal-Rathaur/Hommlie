package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class TrackOrderResponse(

	@field:SerializedName("ratting")
	val ratting: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("data")
	val orderInfo: TrackOrderInfo? = null
)

data class TrackOrderInfo(
	@SerializedName("order_number")
	val orderNumber: String,

	@SerializedName("status")
	val status: Int,

	@SerializedName("product_name")
	val productName: String,

	@SerializedName("qty")
	val quantity: Int,

	@SerializedName("price")
	val price: String,

	@SerializedName("variation")
	val variation: String,

	@SerializedName("discount_amount")
	val discountAmount: String?,

	@SerializedName("shipping_cost")
	val shippingCost: String,

	@SerializedName("order_total")
	val orderTotal: String,

	@SerializedName("full_name")
	val fullName: String,

	@SerializedName("email")
	val email: String,

	@SerializedName("mobile")
	val mobile: String,

	@SerializedName("payment_type")
	val paymentType: Int,

	@SerializedName("order_notes")
	val orderNotes: String?,

	@SerializedName("order_status")
	val orderStatus: Int,

	@SerializedName("landmark")
	val landmark: String,

	@SerializedName("street_address")
	val streetAddress: String,

	@SerializedName("pincode")
	val pincode: String,

	@SerializedName("desired_time")
	val desiredTime: String,

	@SerializedName("desired_date")
	val desiredDate: String,

	@SerializedName("image")
	val image: String,

	@SerializedName("date")
	val date: String,

	@SerializedName("payment")
	val payment: Payment
)

data class Payment(
	@SerializedName("id")
	val id: Int,

	@SerializedName("payment_name")
	val paymentName: String
)
