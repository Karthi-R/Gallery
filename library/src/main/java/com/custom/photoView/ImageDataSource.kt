package com.custom.photoView

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore


import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

import com.custom.photoView.bean.ImageFolder
import com.custom.photoView.bean.ImageItem

import java.io.File
import java.util.ArrayList

class ImageDataSource(private val activity: FragmentActivity) : LoaderManager.LoaderCallbacks<Cursor> {
//    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    private val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA, //  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE, //long型  132492
            MediaStore.Images.Media.WIDTH, // 1920
            MediaStore.Images.Media.HEIGHT, //  1080
            MediaStore.Images.Media.MIME_TYPE, //    image/jpeg
            MediaStore.Images.Media.DATE_ADDED)    //，long 1450518608
    private var loadedListener: OnImagesLoadedListener? = null
    private val imageFolders = ArrayList<ImageFolder>()
    private var currentMode: Int? = null

    fun loadImage(loadedListener: OnImagesLoadedListener) {
        loadImage(null, loadedListener)
    }

    /**
     * @param path
     * @param loadedListener
     */
    fun loadImage(path: String?, loadedListener: OnImagesLoadedListener) {
        this.loadedListener = loadedListener
        destroyLoader()

        val loaderManager = activity.supportLoaderManager
        val bundle = Bundle()
        if (path == null) {
            currentMode = LOADER_ALL
            loaderManager.initLoader(LOADER_ALL, bundle, this)//加载所有的图片
        } else {
            currentMode = LOADER_CATEGORY
            //加载指定目录的图片
            bundle.putString("path", path)
            loaderManager.initLoader(LOADER_CATEGORY, bundle, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        var cursorLoader: CursorLoader? = null

        if (id == LOADER_ALL)
            cursorLoader = CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC")

        if (id == LOADER_CATEGORY)
            if (args != null) {
                cursorLoader = CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'",
                        null, IMAGE_PROJECTION[6] + " DESC")
            }

        return cursorLoader!!
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        imageFolders.clear()
        if (data != null) {
            val allImages = ArrayList<ImageItem>()
            while (data.moveToNext()) {

                val imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                val imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))

                val file = File(imagePath)
                if (!file.exists() || file.length() <= 0) {
                    continue
                }

                val imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                val imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                val imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                val imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))
                val imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))

                val imageItem = ImageItem(imagePath)
                imageItem.size = imageSize
                imageItem.addTime = imageAddTime
                imageItem.height = imageHeight
                imageItem.width = imageWidth
                imageItem.mimeType = imageMimeType
                allImages.add(imageItem)

                val imageFile = File(imagePath)
                val imageParentFile = imageFile.parentFile
                val imageFolder = ImageFolder(imageParentFile.name, imageParentFile.absolutePath)

                if (!imageFolders.contains(imageFolder)) {
                    val images = ArrayList<ImageItem>()
                    images.add(imageItem)
                    imageFolder.cover = imageItem
                    imageFolder.images = images
                    imageFolders.add(imageFolder)
                } else {
                    imageFolders[imageFolders.indexOf(imageFolder)].images.add(imageItem)
                }
            }

            if (data.count > 0 && allImages.size > 0) {

                val allImagesFolder = ImageFolder(activity.resources.getString(R.string.ip_all_images), "/")
                allImagesFolder.cover = allImages[0]
                allImagesFolder.images = allImages
                imageFolders.add(0, allImagesFolder)  //确保第一条是所有图片
            }
        }


        loadedListener?.onImagesLoaded(imageFolders)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        println("--------")

    }

    fun destroyLoader() {
        if (currentMode != null) {
            activity.supportLoaderManager.destroyLoader(currentMode!!)
        }
    }


    interface OnImagesLoadedListener {
        fun onImagesLoaded(imageFolders: List<ImageFolder>)
    }

    companion object {

        val LOADER_ALL = 0
        val LOADER_CATEGORY = 1
    }
}