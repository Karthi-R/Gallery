package com.custom.library.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import com.custom.library.*
import com.custom.library.adapter.ImageFolderAdapter
import com.custom.library.adapter.ImageRecyclerAdapter
import com.custom.library.bean.ImageFolder
import com.custom.library.bean.ImageItem
import com.custom.library.util.CameraUtil
import com.custom.library.view.FolderPopUpWindow
import kotlinx.android.synthetic.main.activity_image_grid.*
import java.io.File

/**
 * Created by hubert
 *
 * Created on 2017/10/12.
 */
class ImageCameraActivity : BaseActivity(), View.OnClickListener {
    companion object {

        val REQUEST_PERMISSION_STORAGE = 0x12
        val REQUEST_PERMISSION_CAMERA = 0x13
        val REQUEST_CAMERA = 0x23
        val REQUEST_PREVIEW = 0x9
        val REQUEST_CROP = 0x10
        val INTENT_MAX = 1000

        /**
         * @param takePhoto 是否直接开启拍照
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
    private lateinit var mFolderPopupWindow: FolderPopUpWindow
    private lateinit var mImageFolderAdapter: ImageFolderAdapter
    private lateinit var imageFolders: List<ImageFolder>
    private lateinit var takeImageFile: File
    private var takePhoto: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_grid)
        //是否直接打开相机
        takePhoto = intent.extras[C.EXTRA_TAKE_PHOTO] as Boolean
        if (takePhoto) {
            onCameraClick()
        }

      //  initView()
      //  initPopWindow()
       // loadData()
    }

/*
    private fun loadData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        } else {
            imageDataSource.loadImage(this)
        }
    }
*/

    override fun onResume() {
        super.onResume()
        //数据刷新
       // adapter.notifyDataSetChanged()
        //onCheckChanged(pickerHelper.selectedImages.size, pickerHelper.limit)
    }

/*
    private fun initView() {
        ll_dir.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_preview.setOnClickListener(this)

        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.addItemDecoration(GridSpacingItemDecoration(3, Utils.dp2px(this, 2f), false))
        adapter = ImageRecyclerAdapter(this, pickerHelper)
        adapter.listener = this
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (tv_date.visibility == View.VISIBLE) {
                        tv_date.animation = AnimationUtils.loadAnimation(this@ImageCameraActivity, R.anim.fade_out)
                        tv_date.visibility = View.GONE
                    }
                } else {
                    if (tv_date.visibility == View.GONE) {
                        tv_date.animation = AnimationUtils.loadAnimation(this@ImageCameraActivity, R.anim.fade_in)
                        tv_date.visibility = View.VISIBLE
                    }
                    val gridLayoutManager = recycler.layoutManager as GridLayoutManager
                    val position = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val addTime = adapter.getItem(position)?.addTime
                    Log.d("hubert", "图片，position：$position ,addTime: $addTime")
                    if (addTime != null) {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = addTime * 1000
                        if (isSameDate(calendar.time, Calendar.getInstance().time)) {
                            tv_date.text = "本周"
                        } else {
                            val format = SimpleDateFormat("yyyy/MM", Locale.getDefault())
                            tv_date.text = format.format(calendar.time)
                        }
                    }
                }
            }
        })

        if (pickerHelper.isMultiMode) {
            btn_ok.visibility = View.VISIBLE
            btn_preview.visibility = View.VISIBLE
        } else {
            btn_ok.visibility = View.GONE
            btn_preview.visibility = View.GONE
        }
    }
*/

    private fun showPopupFolderList() {
        mImageFolderAdapter.refreshData(imageFolders.toMutableList())  //刷新数据
        if (mFolderPopupWindow.isShowing) {
            mFolderPopupWindow.dismiss()
        } else {
            mFolderPopupWindow.showAtLocation(footer_bar, Gravity.NO_GRAVITY, 0, 0)
            //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
            var index = mImageFolderAdapter.selectIndex
            index = if (index == 0) index else index - 1
            mFolderPopupWindow.setSelection(index)
        }
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
            R.id.ll_dir -> showPopupFolderList()
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
