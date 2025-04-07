package com.hommlie.user.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CheckOutDataItem(

    @SerializedName("shipping_cost")
    val shippingCost: String? = null,

    @SerializedName("price")
    val price: String? = null,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("discount_amount")
    val discountAmount: String? = null,

    @SerializedName("product_id")
    val productId: Int? = null,

    @SerializedName("qty")
    val qty: Int? = null,

    @SerializedName("tax")
    val tax: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("attribute")
    val attribute: String? = null,

    @SerializedName("product_name")
    val productName: String? = null,

    @SerializedName("variation")
    val variation: String? = null,

    @SerializedName("vendor_id")
    val vendorId: Int? = null
):Parcelable {
        constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()


        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(shippingCost)
            parcel.writeString(price)
            parcel.writeString(imageUrl)
            parcel.writeString(discountAmount)
            parcel.writeInt(productId!!)
            parcel.writeInt(qty!!)
            parcel.writeString(tax)
            parcel.writeInt(id!!)
            parcel.writeString(attribute)
            parcel.writeString(productName)
            parcel.writeString(variation)
            parcel.writeInt(vendorId!!)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<CheckOutDataItem> {
            override fun createFromParcel(parcel: Parcel): CheckOutDataItem {
                return CheckOutDataItem(parcel)
            }

            override fun newArray(size: Int): Array<CheckOutDataItem?> {
                return arrayOfNulls(size)
            }
        }
}