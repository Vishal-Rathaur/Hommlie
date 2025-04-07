package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class CmsPageResponse(
	@field:SerializedName("privacypolicy")
	val privacypolicy: String? = null,

	@field:SerializedName("refund_policy")
	val refundpolicy: String? = null,

	@field:SerializedName("termsconditions")
	val termsConditions: TermsConditions,

	@field:SerializedName("about")
	val about: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null


)
data class TermsConditions(
	@field:SerializedName("terms_conditions")
	val termsConditions: String
)
