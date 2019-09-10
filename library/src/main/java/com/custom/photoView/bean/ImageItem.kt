package com.custom.photoView.bean

import android.os.Parcel
import android.os.Parcelable

import java.io.Serializable

data class ImageItem(
        var path: String? = null
) : Serializable, Parcelable {

    var name: String? = null
    var size: Long = 0
    var width: Int = 0
    var height: Int = 0
    var mimeType: String? = null
    var addTime: Long = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        name = parcel.readString()
        size = parcel.readLong()
        width = parcel.readInt()
        height = parcel.readInt()
        mimeType = parcel.readString()
        addTime = parcel.readLong()
    }


    override fun equals(other: Any?): Boolean {
        if (other is ImageItem) {
            val item = other as ImageItem?
            if (item != null) {
                return this.path.equals(item.path, ignoreCase = true) && this.addTime == item.addTime
            }
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return path?.hashCode() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(name)
        parcel.writeLong(size)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(mimeType)
        parcel.writeLong(addTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }

}
