package com.custom.photoView.util

import android.content.Context

object ProviderUtil {
    fun getFileProviderName(context: Context): String = context.packageName + ".provider"
}