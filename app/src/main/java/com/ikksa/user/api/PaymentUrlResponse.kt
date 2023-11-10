package com.ikksa.user.api

import com.google.gson.annotations.SerializedName

class PaymentUrlResponse(

    @field:SerializedName("redirect_url")
                         val redirectUrl: String? = null,

                         @field:SerializedName("status")
                         val status: Int? = null
)
