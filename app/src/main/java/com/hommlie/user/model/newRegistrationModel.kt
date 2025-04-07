package com.hommlie.user.model

import com.google.gson.annotations.SerializedName


data class newRegistrationModel(

    val status: Int?=null,
    val message: Message?=null,
    val mobile: String?=null,
    val user_name: String?=null
)

data class Message(
    val request_id: String?=null,
    val type: String?=null
)
