package com.custom.photoView

import android.content.Context
import com.custom.photoView.bean.ImageItem
import com.custom.photoView.loader.ImageLoader
import com.custom.photoView.ui.ShadowActivity
import com.custom.photoView.view.CropImageView

object ImagePicker {
    init {
        println("imagePicker init ...")
    }

    internal var imageLoader: ImageLoader by InitializationCheck("imageLoader is not initialized, please call 'ImagePicker.init(XX)' in your application's onCreate")

    internal var pickHelper: PickHelper = PickHelper()

    private var customPickHelper: PickHelper? = null

    internal var listener: ImagePicker.OnPickImageResultListener? = null


    @JvmStatic
    fun init(imageLoader: ImageLoader) {
        this.imageLoader = imageLoader
    }


    @JvmStatic
    fun defaultConfig(): ImagePicker {
        pickHelper = customPickHelper?.copy() ?: PickHelper()
        return this
    }


    @JvmStatic
    fun saveAsDefault(): ImagePicker {
        customPickHelper = pickHelper
        return this
    }


    @JvmStatic
    fun clear() {
        pickHelper.selectedImages.clear()
        pickHelper.historyImages.clear()
    }


    @JvmStatic
    fun limit(max: Int): ImagePicker {
        pickHelper.limit = max
        return this
    }


    @JvmStatic
    fun showCamera(boolean: Boolean): ImagePicker {
        pickHelper.isShowCamera = boolean
        return this
    }


    @JvmStatic
    fun multiMode(boolean: Boolean): ImagePicker {
        pickHelper.isMultiMode = boolean
        return this
    }


    @JvmStatic
    fun isCrop(boolean: Boolean): ImagePicker {
        pickHelper.isCrop = boolean
        return this
    }


    @JvmStatic
    fun CropConfig(focusStyle: CropImageView.Style, focusWidth: Int, focusHeight: Int, outPutX: Int, outPutY: Int, isSaveRectangle: Boolean) {
        pickHelper.focusStyle = focusStyle
        pickHelper.focusWidth = focusWidth
        pickHelper.focusHeight = focusHeight
        pickHelper.outPutX = outPutX
        pickHelper.outPutY = outPutY
        pickHelper.isSaveRectangle = isSaveRectangle
    }

    @JvmStatic
    fun pick(context: Context, listener: OnPickImageResultListener) {
        this.listener = listener
        ShadowActivity.start(context, 0, 0)
    }

    @JvmStatic
    fun camera(context: Context, listener: OnPickImageResultListener) {
        this.listener = listener
        ShadowActivity.start(context, 2, 0)
    }

    @JvmStatic
    fun review(context: Context, position: Int, listener: OnPickImageResultListener) {
        this.listener = listener
        ShadowActivity.start(context, 1, position)
    }

    interface OnPickImageResultListener {
        fun onImageResult(imageItems: ArrayList<ImageItem>)
    }
}