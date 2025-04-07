package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class SellerMarginResponse(

    @field:SerializedName("data")
    val data: SellerMarginData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class SellerMarginData(

    @field:SerializedName("current_page")
    val currentPage: Int? = null,

    @field:SerializedName("first_page_url")
    val firstPageUrl: String? = null,

    @field:SerializedName("from")
    val from: Int? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("last_page_url")
    val lastPageUrl: String? = null,

    @field:SerializedName("next_page_url")
    val nextPageUrl: Any? = null,

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("per_page")
    val perPage: Int? = null,

    @field:SerializedName("prev_page_url")
    val prevPageUrl: Any? = null,

    @field:SerializedName("to")
    val to: Int? = null,

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("data")
    val data: ArrayList<SellerMargingDataItem>? = null,

    )

data class SellerMargingDataItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_number")
    val orderNumber : String? =null,

    @field:SerializedName("product_id")
    val productId : Int? = null,

    @field:SerializedName("qty")
    val quantity : Int? = null,

    @field:SerializedName("final_price")
    val finalPrice : String? = null,

    @field:SerializedName("reselling_order_flag")
    val resellingOrderFlag : String? =null,

    @field:SerializedName("resell_margin")
    val resellMargin : String?= null,

    @field:SerializedName("price")
    val price : String?= null,

    @field:SerializedName("product_name")
    val productName : String?= null,

    @field:SerializedName("image")
    val image_url : String?= null,

    @field:SerializedName("payment_type")
    val paymentType : Int?= null,

    @field:SerializedName("status")
    val status : Int? = null,

    @field:SerializedName("date")
    val date : String? = null,

    @field:SerializedName("grand_total")
    val grandTotal : Int? = null
)