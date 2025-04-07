package com.hommlie.user.model

import com.google.gson.annotations.SerializedName
import org.bouncycastle.its.asn1.Latitude

data class GetAddressResponse(

	@field:SerializedName("data")
	val data: ArrayList<AddressData>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class AddressData(

	@field:SerializedName("address")
	val streetAddress: String? = null,

	@field:SerializedName("pincode")
	val pincode: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("latitude")
	val latitude:  String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("landmark")
	val landmark: String? = null,

	@field:SerializedName("name")
	val firstName: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
