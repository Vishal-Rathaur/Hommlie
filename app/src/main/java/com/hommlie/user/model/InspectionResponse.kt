package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class InspectionResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: InspectionData
)

data class InspectionData(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("time")
    val time: String,

    @SerializedName("width")
    val width: Float,

    @SerializedName("length")
    val length: Float,

    @SerializedName("sqft")
    val sqft: Float,

    @SerializedName("total_amount")
    val totalAmount: Float
)

