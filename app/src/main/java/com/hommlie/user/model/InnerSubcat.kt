package com.hommlie.user.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class InnerSubcat(

    @SerializedName("id") val id: String?,
    @SerializedName("innersubcategory_name") val innersubcategory_name: String?,
    @SerializedName("innersubcategory_image") val innersubcategory_image:String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(innersubcategory_name)
        parcel.writeString(innersubcategory_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InnerSubcat> {
        override fun createFromParcel(parcel: Parcel): InnerSubcat {
            return InnerSubcat(parcel)
        }

        override fun newArray(size: Int): Array<InnerSubcat?> {
            return arrayOfNulls(size)
        }
    }
}