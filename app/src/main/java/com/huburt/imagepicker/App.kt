package com.huburt.imagepicker

import android.app.Application
import com.huburt.library.ImagePicker

/**
 * Created by hubert
 *
 * Created on 2017/10/24.
 */
class App : Application() {


    companion object {
        lateinit var app: App
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        ImagePicker.init(GlideImageLoader())
        //保存为自定义默认
        ImagePicker.limit(10).isCrop(true).saveAsDefault()
    }
}