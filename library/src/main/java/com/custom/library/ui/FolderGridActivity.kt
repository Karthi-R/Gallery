package com.custom.library.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.custom.library.*
import com.custom.library.adapter.PagerAdapter
import com.custom.library.bean.ImageFolder
import com.custom.library.bean.ImageItem
import com.custom.library.util.CameraUtil
import kotlinx.android.synthetic.main.activity_folder_grid.*
import kotlinx.android.synthetic.main.activity_folder_grid.btn_ok
import java.io.File


class FolderGridActivity : BaseActivity(), View.OnClickListener, ImageDataSource.OnImagesLoadedListener {

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
            val intent = Intent(activity, FolderGridActivity::class.java)
            intent.putExtra(C.EXTRA_TAKE_PHOTO, takePhoto)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private val pickerHelper: PickHelper = ImagePicker.pickHelper
    private lateinit var imageFolders: List<ImageFolder>
    private val imageDataSource = ImageDataSource(this)
    private lateinit var takeImageFile: File
    private var takePhoto: Boolean = false
    var viewPager: ViewPager? = null
    var adapter: PagerAdapter? = null


    override fun onClick(p0: View?) {

    }

    override fun onImagesLoaded(imageFolders: List<ImageFolder>) {
        this.imageFolders = imageFolders
        showPopupFolderList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_grid)

        //Find View By Id For GridView
        val gridView = findViewById(R.id.gridView) as GridView
        loadData()



        btn_preview.setOnClickListener {
            if(pickerHelper.selectedImages.size>0){
                ImagePreviewActivity.startForResult(this, ImageGridFragment.REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
            } else{
                showToast("Select image to preview")
            }
        }


        btn_ok.setOnClickListener {
                val result = Intent()
                result.putExtra(C.EXTRA_IMAGE_ITEMS, pickerHelper.selectedImages)
                this.setResult(Activity.RESULT_OK, result)
                this.finish()
        }



        /*Create and ArrayList of Integer Type To Store Images From drawable.Here we add Images to ArrayList.
        We have Images of Android Icons of Different versions.
        */

        val arrayListImage = ArrayList<Int>()

      
/*
        mFolderPopupWindow.setOnItemClickListener(object : FolderPopUpWindow.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                initView()
                mImageFolderAdapter.selectIndex = position
                mFolderPopupWindow.dismiss()
                val imageFolder = adapterView.adapter?.getItem(position) as ImageFolder
                adapter.refreshData(imageFolder.images)
                tv_dir.text = imageFolder.name
            }
        })

 */


        gridView.setOnItemClickListener { adapterView, parent, position, l ->
            val intent = Intent(this, ImageGridActivity::class.java)
            intent.putExtra("Selected_position", position)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
       // showPopupFolderList()
    }

    private fun showPopupFolderList() {

        viewPager = findViewById(R.id.recycleview_pager)

      //x  val imageDataSource = this?.let { ImageDataSource(it as FragmentActivity) }

        adapter = PagerAdapter(this,supportFragmentManager, imageFolders.toMutableList(),imageDataSource)
        viewPager!!.setAdapter(adapter)

       // Toast.makeText(this, ""+ viewPager!!.currentItem, Toast.LENGTH_SHORT).show()

        tabLayout.tabMode =  TabLayout.MODE_SCROLLABLE
        tabLayout.setupWithViewPager(viewPager)


        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))



      /*  tabLayout.onTabSelected {
            trackTabChange(tabLayout.getTabAt(it))
        }

        viewPager.setCurrentItem(mTabPosition, false)*/

       // viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

      //  tabLayout.se


    }



/*
    private fun showPopupFolderList() {
      //  mImageFolderAdapter.refreshData(imageFolders.toMutableList())  //刷新数据
        val customAdapter = FolderGridAdapter(this@FolderGridActivity, imageFolders.toMutableList())

        //Set Adapter to ArrayList

        gridView.adapter = customAdapter

    }
*/


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FolderGridActivity.REQUEST_PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageDataSource.loadImage(this)
            } else {
                showToast("权限被禁止，无法选择本地图片")
            }
        } else if (requestCode == FolderGridActivity.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImageFile = CameraUtil.takePicture(this, FolderGridActivity.REQUEST_CAMERA)
            } else {
                showToast("权限被禁止，无法打开相机")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FolderGridActivity.REQUEST_CAMERA) {//相机返回
            if (resultCode == Activity.RESULT_OK) {
                takeImageFile = CameraUtil.takePicture(this, FolderGridActivity.REQUEST_CAMERA)

                Log.e("hubert", takeImageFile.absolutePath)
                //广播通知新增图片
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(takeImageFile)
                sendBroadcast(mediaScanIntent)

                val imageItem = ImageItem(takeImageFile.absolutePath)
               /// pickerHelper.selectedImages.clear()
               // pickerHelper.selectedImages.add(imageItem)

                /*if (pickerHelper.isCrop) {//需要裁剪
                    ImageCropActivity.start(this, FolderGridActivity.REQUEST_CROP)
                } else {*/
                    setResult()
               // }
            } else if (takePhoto) {//直接拍照返回时不再展示列表
                finish()
            }
        } else if (requestCode == FolderGridActivity.REQUEST_PREVIEW) {//预览界面返回
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        } else if (requestCode == FolderGridActivity.REQUEST_CROP) {//裁剪结果
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

    private fun loadData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), FolderGridActivity.REQUEST_PERMISSION_STORAGE)
        } else {
            imageDataSource.loadImage(this)
        }
    }


}