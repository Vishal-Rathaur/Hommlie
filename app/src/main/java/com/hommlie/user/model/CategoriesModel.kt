package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(

    @field:SerializedName("data")
	val data: ArrayList<DataItem>? = null,

	@SerializedName("app_header")
	val appHeader: HeaderDetails,

	@SerializedName("shop_header")
	val shopHeader: HeaderDetails,

    @field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("status")
	val status: Int? = null
)

data class DataItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category_name")
	val categoryName: String? = null,

	@field:SerializedName("app_icon")
	val imageUrl: String? = null,

	@field:SerializedName("video")
	val video: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("is_form")
	val isForm: Int? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	@field:SerializedName("alt_tag")
	val altTag: String? = null,

	@field:SerializedName("image_title")
	val imageTitle: String? = null,

	@field:SerializedName("meta_title")
	val metaTitle: String? = null,

	@field:SerializedName("meta_description")
	val metaDescription: String? = null,

	@field:SerializedName("motion_graphics")
	val motionGraphics: String? = null
)

data class HeaderDetails(
	@SerializedName("id") val id: Int,
	@SerializedName("bg_color") val bgColor: String,
	@SerializedName("image") val image: String,
	@SerializedName("text_color") val textColor: String,
	@SerializedName("sub_text_color") val subTextColor: String,
	@SerializedName("status")
	val status : Int? = null
)
