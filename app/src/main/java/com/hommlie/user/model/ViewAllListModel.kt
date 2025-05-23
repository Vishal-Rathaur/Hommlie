package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class ViewAllListResponse(

	@field:SerializedName("data")
	val alldata: ArrayList<ViewAllDataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ViewAllProductimage(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ViewAllDataItem(

	@field:SerializedName("rattings")
	val rattings: ArrayList<ViewAllRattingsItem>? = null,

	@field:SerializedName("is_variation")
	val isVariation: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("product_price")
	val productPrice: String? = null,

	@field:SerializedName("sku")
	val sku: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("is_wishlist")
	var isWishlist: Int? = null,

	@field:SerializedName("productimage")
	val productimage: ViewAllProductimage? = null,

	@field:SerializedName("variation")
	val variation: ViewAllVariation? = null,

	@field:SerializedName("discounted_price")
	val discountedPrice: String? = null,

	@field:SerializedName("rating")
	val rating: Float? = null,
)

data class ViewAllRattingsItem(

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("avg_ratting")
	val avgRatting: String? = null
)

data class ViewAllData(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<ViewAllDataItem>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: Any? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

data class ViewAllVariation(

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("discounted_variation_price")
	val discountedVariationPrice: String? = null,

	@field:SerializedName("variation")
	val variation: String? = null
)
