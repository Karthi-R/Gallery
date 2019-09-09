package com.custom.gallery

import android.app.Application
import com.custom.library.ImagePicker


class App : Application() {

    companion object {
        lateinit var app: App
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        ImagePicker.init(GlideImageLoader())
        ImagePicker.limit(10).isCrop(true).saveAsDefault()
    }
}