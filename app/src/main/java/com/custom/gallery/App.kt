package com.custom.gallery

import android.app.Application
import com.custom.library.ImagePicker
import com.facebook.stetho.Stetho
import com.squareup.picasso.Picasso
import timber.log.Timber


class App : Application() {

    companion object {
        lateinit var app: App
        lateinit var picInstance: Picasso
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        picInstance= Picasso.get()
        ImagePicker.init(GlideImageLoader())
        ImagePicker.limit(10).isCrop(true).saveAsDefault()
        initTimber()
        initStetho()
    }

    private fun initStetho() {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build())
        }


    protected open fun initTimber() {
        val debug = BuildConfig.DEBUG
        Timber.plant(if (debug) Timber.DebugTree() else CrashReportingTree())
    }

}