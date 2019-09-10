package com.custom.gallery

import android.app.Activity
import android.net.Uri
import android.widget.ImageView

import com.custom.photoView.R
import com.custom.photoView.loader.ImageLoader
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File
import java.lang.Exception


class GlideImageLoader : ImageLoader {

    override fun displayImage(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        val mWidth = 250
        App.picInstance.load((Uri.fromFile(File(path))))?.resize(mWidth, mWidth)?.centerCrop()?.placeholder(R.drawable.ic_default_image)?.into(imageView)
    }

    override fun displayImagePreview(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        App.picInstance.load((Uri.fromFile(File(path))))?.resize(250, 0)?.placeholder(R.drawable.ic_default_image)?.into(imageView)
    }

    override fun clearMemoryCache() {

    }
}