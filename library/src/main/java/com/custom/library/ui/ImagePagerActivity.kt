package com.custom.library.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.custom.library.R
import com.custom.library.adapter.PagerAdapter

/**
 * Created by Kumuthini N on 2019-08-29.
 */
class ImagePagerActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var adapter: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

       /* viewPager = findViewById(R.id.id_recycleview_pager)
        adapter = PagerAdapter(supportFragmentManager, 6)
        viewPager!!.setAdapter(adapter)*/
    }

}