package com.custom.library.util

import android.content.Context
import android.os.Environment
import java.io.File
import kotlin.random.Random


object FileUtil {

    private fun getStorageDir(context: Context): String? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return storageDir?.let { it.absolutePath + File.separator }
    }

   fun getCropCacheFolder(context: Context): File {
       val file =File(getStorageDir(context) + "/SelectedImages/")
       if (file?.exists() == false) file.mkdirs()
       return file
   }
}