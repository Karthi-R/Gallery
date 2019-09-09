package com.custom.gallery

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.custom.library.CropView.CropImage
import com.custom.library.ImagePicker
import com.custom.library.bean.ImageItem
import com.custom.library.util.Utils
import com.custom.library.view.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ImagePicker.OnPickImageResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImagePicker.defaultConfig()
        cb_crop.setOnCheckedChangeListener({ _, isChecked -> ImagePicker.isCrop(isChecked) })
        cb_multi.isChecked = true
        cb_multi.setOnCheckedChangeListener { _, isChecked -> ImagePicker.multiMode(isChecked) }
        ImagePicker.multiMode(true)
        btn_pick.setOnClickListener {
            ImagePicker.pick(this@MainActivity, this@MainActivity)
        }
        btn_camera.setOnClickListener {
            ImagePicker.camera(this@MainActivity, this@MainActivity)
        }
        recycler_view.layoutManager = LinearLayoutManager(this)
        val imageAdapter = ImageAdapter(ArrayList())
        imageAdapter.listener = object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                ImagePicker.review(this@MainActivity, position, this@MainActivity)
            }
        }
        recycler_view.adapter = imageAdapter
    }

    override fun onImageResult(imageItems: ArrayList<ImageItem>) {
        (recycler_view.adapter as ImageAdapter).updateData(imageItems)
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePicker.clear()
    }
}
