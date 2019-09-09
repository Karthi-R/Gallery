package com.custom.library

import com.custom.library.bean.ImageItem
import com.custom.library.view.CropImageView
import java.io.Serializable
import java.util.*


data class PickHelper(
        var limit: Int = 9,
        var isCrop: Boolean = true,
        var isShowCamera: Boolean = true,
        var isMultiMode: Boolean = true
) : Serializable {

    var focusStyle = CropImageView.Style.RECTANGLE
    var outPutX = 800
    var outPutY = 800
    var focusWidth = 280
    var focusHeight = 280
    var isSaveRectangle = false

    val selectedImages: ArrayList<ImageItem> = ArrayList()
    val historyImages: ArrayList<ImageItem> = ArrayList()

    fun canSelect(): Boolean = selectedImages.size < limit

}