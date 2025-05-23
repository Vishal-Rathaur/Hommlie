package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class InerrSubCategoriesResponse(

	@field:SerializedName("data")
	val data: List<InnerSubcat>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)/*: ParentListItem {
	override fun getChildItemList(): List<*> = data!!
	override fun isInitiallyExpanded(): Boolean = false
}*/
data class InnerSubCateDataItem(

	@field:SerializedName("innersubcategory_name")
	val innersubcategoryName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
