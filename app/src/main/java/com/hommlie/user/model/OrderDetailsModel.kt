package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class OrderDetailsResponse(

	@field:SerializedName("order_data")
	val orderData: ArrayList<OrderDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("order_info")
	val orderInfo: OrderInfo? = null,
)

data class OrderInfo(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("street_address")
	val streetAddress: String? = null,

	@field:SerializedName("pincode")
	val pincode: String? = null,

	@field:SerializedName("order_notes")
	val orderNotes: String? = null,

	@field:SerializedName("shipping_cost")
	val shippingCost: String? = null,

	@field:SerializedName("discount_amount")
	val discountAmount: String? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("tax")
	val tax: String? = null,

	@field:SerializedName("coupon_name")
	val couponName: String? = null,

	@field:SerializedName("desired_time")
	val desired_time: String? = null,

	@field:SerializedName("desired_date")
	val desired_date: String? = null,

	@field:SerializedName("payment_type")
	val paymentType: Int? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("subtotal")
	val subtotal: String? = null,

	@field:SerializedName("grand_total")
	val grandTotal: String? = null,

	@field:SerializedName("landmark")
	val landmark: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("order_status")
	val status: Int? = null,

//	@field:SerializedName("resell_margin")
//    val marginEarned: String? = null,
//
//	@field:SerializedName("final_price")
//	val finalPrice: String? = null
)

data class OrderDataItem(

	@field:SerializedName("shipping_cost")
	val shippingCost: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("discount_amount")
	val discountAmount: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("tax")
	val tax: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("attribute")
	val attribute: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("variation")
	val variation: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

//	@field:SerializedName("final_price")
//	val finalPrice: String? = null
)
