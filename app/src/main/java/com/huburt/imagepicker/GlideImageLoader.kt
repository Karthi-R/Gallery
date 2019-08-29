package com.huburt.imagepicker

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.huburt.library.Edit.CropImageView
import com.huburt.library.R
import com.huburt.library.loader.ImageLoader
import java.io.File

/**
 * Created by hubert
 *
 * Created on 2017/10/12.
 */
class GlideImageLoader : ImageLoader {

    override fun displayImage(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.ic_default_image)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(imageView)
    }

    override fun displayImagePreview(activity: Activity, path: String, imageView: CropImageView, width: Int, height: Int) {
       /* Glide.with(activity)
                .load(Uri.fromFile(File(path)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)*/

        imageView.setImageUriAsync(Uri.fromFile(File(path)))
    }

    override fun clearMemoryCache() {

    }
}