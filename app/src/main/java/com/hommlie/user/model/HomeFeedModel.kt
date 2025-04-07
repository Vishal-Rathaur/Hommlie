package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class HomefeedResponse(

    @field:SerializedName("cleaning_services")
    val cleaningServices: ArrayList<CleaningServicesItem>? = null,

    @field:SerializedName("mosquito_mesh")
    val mosquitomess: ArrayList<CleaningServicesItem>? = null,

    @field:SerializedName("videos")
    val homeAddVideo: ArrayList<HomeAddVideo>?=null,

    @field:SerializedName("testimonials")
    val testimonialsList: ArrayList<Testimonials>? = null,

    @field:SerializedName("offer_below_categories")
    val offer_below_categories : ArrayList<OfferImage>? = null,

    @field:SerializedName("safety_pro_netting")
    val safetyProNetting: ArrayList<CleaningServicesItem>? = null,

    @field:SerializedName("shop_now")
    val shopNow: ArrayList<ShopNowDataItem>? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("most_booked_services")
    val hotProducts: ArrayList<HotProductsItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("currency_position")
    val currencyPosition: String? = null,

    @field:SerializedName("pest_control")
    val pestControl: ArrayList<CleaningServicesItem>? = null,

    @field:SerializedName("notifications")
    val notifications: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class VendorsItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class CleaningServicesItem(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("subcategory_name")
    val subcategoryName: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null
)

data class FeaturedProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

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
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Any? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null,
    var isChecked: Int = 0
)

data class Variation(

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

data class HotProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

    @field:SerializedName("is_variation")
    val isVariation: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_count")
    val order_count: Int? = null,

    @field:SerializedName("product_price")
    val productPrice: String? = null,

    @field:SerializedName("sku")
    val sku: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null,

    @field:SerializedName("is_wishlist")
    var isWishlist: Int? = null,

    @field:SerializedName("productimage")
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Variation? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null,

    @field:SerializedName("rating")
    val rating: Float? = null,
)

data class Productimage(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class NewProductsItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

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
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Variation? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class ShopNowDataItem(

    @field:SerializedName("rattings")
    val rattings: ArrayList<Rattings>? = null,

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
    val productimage: Productimage? = null,

    @field:SerializedName("variation")
    val variation: Variation? = null,

    @field:SerializedName("discounted_price")
    val discountedPrice: String? = null
)

data class BrandsItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("brand_name")
    val brandName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

data class OfferImage(
    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("offer_text")
    val offertext: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("bg_color")
    val bg_color : String? = null
)

data class RattingsItem(

    @field:SerializedName("product_id")
    val productId: Int? = null,

    @field:SerializedName("avg_ratting")
    val avgRatting: String? = null

)

data class Testimonials(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("feedback")
    val feedback : String? = null,

    @field:SerializedName("image")
    val image : String? = null,

    var isExpanded: Boolean = false
)

data class HomeAddVideo(
//    @field:SerializedName("media")
//    val media: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("thumbnail")
    val thumbnail : String? = null,

    @field:SerializedName("video")
    val video_url : String? = null
)
