package com.custom.library.ui

import android.os.Bundle
import com.custom.library.ImagePicker
import com.custom.library.PickHelper
import com.custom.library.R
import com.custom.library.adapter.ImagePagerAdapter
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.android.synthetic.main.include_top_bar.*
import uk.co.senab.photoview.PhotoViewAttacher

abstract class ImagePreviewBaseActivity : BaseActivity(), PhotoViewAttacher.OnPhotoTapListener {


    protected lateinit var imagePagerAdapter: ImagePagerAdapter
    protected var pickHelper: PickHelper = ImagePicker.pickHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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