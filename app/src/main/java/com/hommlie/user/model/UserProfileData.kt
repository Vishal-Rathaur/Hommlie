package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class UserProfileData(

    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("cartLength")
    val cartLength: Int,

    @SerializedName("walletBalance")
    val walletBalance: Double,

    @SerializedName("data")
    val data_user: UserProfile,

    @SerializedName("contactInfo")
    val data_contactInfo: UserContactInfo

)

data class UserContactInfo(

    @SerializedName("email")
    val contactEmail: String?,
    @SerializedName("address")
    val contactAddress: String?,
    @SerializedName("contact")
    val contact_mobile: String?,
    @SerializedName("facebook")
    val facebook: String?,
    @SerializedName("linkedin")
    val linkedin: String?,// You can replace the type with the actual type
    @SerializedName("instagram")
    val instagram: String?, // You can replace the type with the actual type
    @SerializedName("twitter")
    val twitter: String?, // You can replace the type with the actual type
    @SerializedName("google")
    val google: String?, // You can replace the type with the actual type
    @SerializedName("youtube")
    val youtube: String?, // You can replace the type with the actual type
    )

data class UserProfile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?=null,
    @SerializedName("email")
    val email: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("store_address")
    val storeAddress: String?,
    @SerializedName("store_lat_long")
    val storeLatLong: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("notification_status")
    val notificationStatus: String,
    @SerializedName("type")
    val type: String,

    @SerializedName("login_type")
    val loginType: String,
    @SerializedName("referral_code")
    val referralCode: String,
    @SerializedName("referral_amount")
    val referralAmount: String?,
    @SerializedName("wallet")
    val wallet: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("otp")
    val otp: String?,
    @SerializedName("is_verified")
    val isVerified: String,
    @SerializedName("is_available")
    val isAvailable: String,
    @SerializedName("return_policies")
    val returnPolicies: String?, // You can replace the type with the actual type

    @SerializedName("bank_name")
    val bankName: String,
    @SerializedName("bank_ifsc")
    val bankIFSC: String,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("shop_name")
    val shopName: String,
    @SerializedName("shop_mobile")
    val shopMobile: String,
    @SerializedName("shop_email")
    val shopEmail: String,
    @SerializedName("shop_address")
    val shopAddress: String,
    @SerializedName("shop_gst")
    val shopGST: String,
    @SerializedName("social_media_link")
    val socialMediaLink: String,
    @SerializedName("shop_pic")
    val shopPic: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("upi")
    val upi: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

