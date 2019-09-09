package com.custom.gallery

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.custom.library.R
import com.custom.library.loader.ImageLoader
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.Exception


class GlideImageLoader : ImageLoader {

    override fun displayImage(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        Glide.with(activity)
                .load(Uri.fromFile(File(path)))
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
    }

    override fun displayImagePreview(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        Glide.with(activity)
                .load(Uri.fromFile(File(path)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
    }

    override fun clearMemoryCache() {

    }
}