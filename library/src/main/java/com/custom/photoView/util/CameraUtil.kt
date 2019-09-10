package com.custom.photoView.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object CameraUtil {
    fun takePicture(activity: Activity, requestCode: Int): File {
        var takeImageFile =
                if (Utils.existSDCard())
                    File(Environment.getExternalStorageDirectory(), "/DCIM/camera/")
                else
                    Environment.getDataDirectory()
        takeImageFile = createFile(takeImageFile, "IMG_", ".jpg")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {

            val uri: Uri
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                uri = Uri.fromFile(takeImageFile)
            } else {

                uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), takeImageFile)
                val resInfoList = activity.packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                resInfoList
                        .map { it.activityInfo.packageName }
                        .forEach { activity.grantUriPermission(it, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        activity.startActivityForResult(takePictureIntent, requestCode)
        return takeImageFile
    }


    fun createFile(folder: File, prefix: String, suffix: String): File {
        if (!folder.exists() || !folder.isDirectory) folder.mkdirs()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val filename = prefix + dateFormat.format(Date(System.currentTimeMillis())) + suffix
        return File(folder, filename)
    }
}