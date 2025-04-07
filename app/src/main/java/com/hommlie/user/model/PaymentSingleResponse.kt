package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class PaymentSingleResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,
)
