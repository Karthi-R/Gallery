package com.huburt.library.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Created by hubert
 *
 * Created on 2017/10/31.
 */
object FileUtil {

    fun getStorageDir(context: Context): String? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir?.exists() == false) storageDir.mkdirs()
        return storageDir?.let { it.absolutePath + File.separator }
    }


/*
    fun getCropCacheFolder(context: Context): File {
        return File(context.cacheDir.toString() + "/ImagePicker/cropTemp/")
    }
*/
    fun getCropCacheFolder(context: Context): File {
        return File(getStorageDir(context) + "/ImagePicker/cropTemp/")
    }
}