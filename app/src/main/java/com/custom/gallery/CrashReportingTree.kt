package com.custom.gallery

import android.util.Log
import android.util.Log.ERROR
import android.util.Log.WARN
//import com.crashlytics.android.Crashlytics
import timber.log.Timber


class CrashReportingTree: Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            //Crashlytics.log(priority, tag, message)
            return
        }

        if (priority == ERROR || priority == WARN) {
           // Crashlytics.log(priority, tag, message)

            val t = throwable ?: Exception(message)
           // Crashlytics.logException(t)
        }
    }
}