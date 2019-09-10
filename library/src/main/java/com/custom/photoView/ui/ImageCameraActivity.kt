package com.custom.photoView.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.custom.photoView.*
import com.custom.photoView.adapter.ImageRecyclerAdapter
import com.custom.photoView.bean.ImageFolder
import com.custom.photoView.bean.ImageItem
import com.custom.photoView.util.CameraUtil
import java.io.File

class ImageCameraActivity : BaseActivity(), View.OnClickListener {
    companion object {

        val REQUEST_PERMISSION_STORAGE = 0x12
        val REQUEST_PERMISSION_CAMERA = 0x13
        val REQUEST_CAMERA = 0x23
        val REQUEST_PREVIEW = 0x9
        val REQUEST_CROP = 0x10
        val INTENT_MAX = 1000

        /**
         * @param takePhoto
         */
        fun startForResult(activity: Activity, requestCode: Int, takePhoto: Boolean) {
            val intent = Intent(activity, ImageCameraActivity::class.java)
            intent.putExtra(C.EXTRA_TAKE_PHOTO, takePhoto)
            activity.startActivityForResult(intent, requestCode)
           // activity.finish()
        }
    }

    private val pickerHelper: PickHelper = ImagePicker.pickHelper
    private val imageDataSource = ImageDataSource(this)
    private lateinit var adapter: ImageRecyclerAdapter
    private lateinit var imageFolders: List<ImageFolder>
    private lateinit var takeImageFile: File
    private var takePhoto: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_grid)
        takePhoto = intent.extras[C.EXTRA_TAKE_PHOTO] as Boolean
        if (takePhoto) {
            onCameraClick()
        }

    }


    override fun onResume() {
        super.onResume()
    }


     fun onCameraClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    ImageCameraActivity.REQUEST_PERMISSION_CAMERA)
        } else {
            takeImageFile = CameraUtil.takePicture(this, REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImageFile = CameraUtil.takePicture(this, REQUEST_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {//相机返回
            if (resultCode == Activity.RESULT_OK) {
                Log.e("hubert", takeImageFile.absolutePath)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(takeImageFile)
                sendBroadcast(mediaScanIntent)

                val imageItem = ImageItem(takeImageFile.absolutePath)
                pickerHelper.selectedImages.clear()
                pickerHelper.selectedImages.add(imageItem)

                if (pickerHelper.isCrop) {//需要裁剪
                    ImagePreviewActivity.startForResult(this, REQUEST_PREVIEW, 0, pickerHelper.selectedImages)

                }
                /*else {
                    setResult()
                }*/
            } else if (takePhoto) {//直接拍照返回时不再展示列表
                finish()
            }
        } else if (requestCode == REQUEST_PREVIEW) {//预览界面返回
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        } else if (requestCode == REQUEST_CROP) {//裁剪结果
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        }
    }

    private fun setResult() {
        val result = Intent()
        result.putExtra(C.EXTRA_IMAGE_ITEMS, pickerHelper.selectedImages)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
          //  R.id.ll_dir -> showPopupFolderList()
            R.id.btn_ok -> setResult()
            R.id.btn_preview -> ImagePreviewActivity.startForResult(this, REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
            R.id.btn_back -> {
               // setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

/*
    override fun onImagesLoaded(imageFolders: List<ImageFolder>) {
        if(imageFolders.size>0){
            this.imageFolders = imageFolders
            adapter.refreshData(imageFolders[0].images)
            recycler.adapter = adapter
        }
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        imageDataSource.destroyLoader()
    }

}
