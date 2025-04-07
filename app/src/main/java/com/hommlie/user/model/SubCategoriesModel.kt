package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class SubCategoriesResponse(

    @field:SerializedName("data")
    val data: SubCateData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class SubcategoryItem(

    @field:SerializedName("subcat_id")
    val subcatId: Int? = null,

    @field:SerializedName("subcategory_name")
    val subcategoryName: String? = null,

    @field:SerializedName("subcategory_title")
    val subcategory_title: String? = null,

    @field:SerializedName("subcategory_sub_title")
    val subcategory_sub_title: String? = null,

    @field:SerializedName("productsData")
    val innersubcategory: ArrayList<ProductDataItem>? = null,

    @field:SerializedName("subcategory_icon")
    val subcatImage: String? = null,

    var expand: Boolean = false
)

data class SubCateData(

    @field:SerializedName("subcategory")
    val subcategory: ArrayList<SubcategoryItem>? = null,

    @field:SerializedName("categoryData")
    val mainCategoryData: MainCategoryData? = null
)


data class MainCategoryData(
    @SerializedName("id") val id: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("video") val video: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("slug") val slug: String,
    @SerializedName("alt_tag") val altTag: String?,
    @SerializedName("image_title") val imageTitle: String?,
    @SerializedName("meta_title") val metaTitle: String?,
    @SerializedName("meta_description") val metaDescription: String?,
    @SerializedName("motion_graphics") val motionGraphics: String?,
    @SerializedName("specifications") val specifications: String?,
    @SerializedName("banner") val banner: String?,
    @SerializedName("total_reviews") val totalReviews: Int,
    @SerializedName("avg_rating") val avgRating: Double,
    @SerializedName("faqs") val faqs: String?,
    @SerializedName("other_services") val otherServices: List<OtherService>
)


data class OtherService(
    @SerializedName("id") val id: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("slug") val slug: String
)

data class InnersubcategoryItem(

    @field:SerializedName("innersubcategory_name")
    val innersubcategoryName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
