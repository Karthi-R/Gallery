package com.custom.library.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.custom.library.*
import com.custom.library.adapter.PagerAdapter
import com.custom.library.bean.ImageFolder
import com.custom.library.util.CameraUtil
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_folder_grid.*
import java.io.File


class GalleryDetailActivity : BaseActivity(), View.OnClickListener, ImageDataSource.OnImagesLoadedListener {

    companion object {

        val REQUEST_PERMISSION_STORAGE = 0x12
        val REQUEST_PERMISSION_CAMERA = 0x13
        val REQUEST_CAMERA = 0x23
        val REQUEST_PREVIEW = 0x9


        /**
         * @param takePhoto
         */
        fun startForResult(activity: Activity, requestCode: Int, takePhoto: Boolean) {
            val intent = Intent(activity, GalleryDetailActivity::class.java)
            intent.putExtra(C.EXTRA_TAKE_PHOTO, takePhoto)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private val pickerHelper: PickHelper = ImagePicker.pickHelper
    private lateinit var imageFolders: List<ImageFolder>
    private val imageDataSource = ImageDataSource(this)
    private lateinit var takeImageFile: File
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var upArrow = getResources().getDrawable(R.drawable.ic_arrow_back)
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
        getSupportActionBar()?.setHomeAsUpIndicator(upArrow)


        btn_preview.setOnClickListener {
            if (pickerHelper.selectedImages.size > 0) {
                ImagePreviewActivity.startForResult(this, REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
            } else {
                showToast("Select image to preview")
            }
        }


        btn_ok.setOnClickListener {
            val result = Intent()
            result.putExtra(C.EXTRA_IMAGE_ITEMS, pickerHelper.selectedImages)
            this.setResult(Activity.RESULT_OK, result)
            this.finish()
        }


        gridView.setOnItemClickListener { adapterView, parent, position, l ->
            val intent = Intent(this, ImageCameraActivity::class.java)
            intent.putExtra("Selected_position", position)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun showPopupFolderList() {

        viewPager = findViewById(R.id.recycleview_pager)

        adapter = PagerAdapter(this, supportFragmentManager, imageFolders.toMutableList(), imageDataSource)
        viewPager!!.setAdapter(adapter)

        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.setupWithViewPager(viewPager)

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GalleryDetailActivity.REQUEST_PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageDataSource.loadImage(this)
            }
        } else if (requestCode == GalleryDetailActivity.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImageFile = CameraUtil.takePicture(this, REQUEST_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PREVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        } else {
            for (fragment in supportFragmentManager.getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data)
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
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), GalleryDetailActivity.REQUEST_PERMISSION_STORAGE)
        } else {
            imageDataSource.loadImage(this)
        }
    }
}