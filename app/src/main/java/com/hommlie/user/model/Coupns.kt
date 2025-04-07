package com.hommlie.user.model


import com.google.gson.annotations.SerializedName

data class Coupns(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Coupon>
)

data class Coupon(
    @SerializedName("id") val id: Int,
    @SerializedName("coupon_name") val couponName: String,
    @SerializedName("type") val type: String,
    @SerializedName("percentage") val percentage: String,
    @SerializedName("amount") val amount: String?,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("times") val times: String?,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
