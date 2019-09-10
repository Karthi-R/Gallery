package com.custom.photoView.ui

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.custom.photoView.ImagePicker
import com.custom.photoView.PickHelper
import com.custom.photoView.R
import com.custom.photoView.adapter.ImagePagerAdapter
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.android.synthetic.main.include_top_bar.*
import com.custom.photoView.photoTap.PhotoViewAttacher


abstract class ImagePreviewBaseActivity : BaseActivity(), PhotoViewAttacher.OnPhotoTapListener {


    protected lateinit var imagePagerAdapter: ImagePagerAdapter
    protected var pickHelper: PickHelper = ImagePicker.pickHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideStatusBar()

        setContentView(R.layout.activity_image_preview)
        initView()
    }

    private fun initView() {
        btn_back.setOnClickListener { finish() }

        imagePagerAdapter = ImagePagerAdapter(this)
        imagePagerAdapter.listener = this

        viewpager.adapter = imagePagerAdapter
    }

}