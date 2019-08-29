package com.huburt.library.ui

import android.os.Bundle
import com.huburt.library.ImagePicker
import com.huburt.library.PickHelper
import com.huburt.library.R
import com.huburt.library.adapter.ImagePagerAdapter
import com.huburt.library.adapter.PagerAdapter
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.android.synthetic.main.include_top_bar.*
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by hubert
 *
 * Created on 2017/10/24.
 */
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