package com.custom.library.bean

import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ImageFolder(var name: String?,
                       var path: String?,
                       var cover: ImageItem? = null,
                       var images: ArrayList<ImageItem> = ArrayList()
) : Serializable, Parcelable {


    override fun equals(other: Any?): Boolean {
        try {
            val item = other as ImageFolder?
            return this.path!!.equals(item!!.path!!, ignoreCase = true) && this.name!!.equals(item.name!!, ignoreCase = true)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return path?.hashCode() ?: 0
    }
}
