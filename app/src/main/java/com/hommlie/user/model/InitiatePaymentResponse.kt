package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class InitiatePaymentResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("data")
    val data: OrderInitiateData? = null
)

data class OrderInitiateData(
    @field:SerializedName("amount")
    val amount: Int? = null,

    @field:SerializedName("amount_due")
    val amountDue: Int? = null,

    @field:SerializedName("amount_paid")
    val amountPaid: Int? = null,

    @field:SerializedName("attempts")
    val attempts: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: Long? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("entity")
    val entity: String? = null,

    @field:SerializedName("id")
    val orderId: String? = null,

    @field:SerializedName("notes")
    val notes: List<String>? = null,

    @field:SerializedName("offer_id")
    val offerId: String? = null,

    @field:SerializedName("receipt")
    val receipt: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
