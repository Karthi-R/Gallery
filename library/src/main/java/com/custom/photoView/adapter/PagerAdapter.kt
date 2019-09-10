package com.custom.photoView.adapter

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.custom.photoView.ImageDataSource
import com.custom.photoView.bean.ImageFolder
import com.custom.photoView.ui.GalleryFragment
import com.custom.photoView.util.Utils
import java.util.ArrayList

/**
 * Created by Kumuthini N on 2019-08-29.
 */
class PagerAdapter(private val mActivity: Activity, fm: FragmentManager, folders: MutableList<ImageFolder>?, imageDataSource: ImageDataSource) : FragmentStatePagerAdapter(fm) {

    private var itensCount = 0
    private val mImageSize: Int
    private var imageFolders: MutableList<ImageFolder>? = null
    var imageDataSource: ImageDataSource? = null
   // private var imageDataSource: ImageDataSource? = null


    var selectIndex = 0
        set(i) {
            if (selectIndex != i) {
                field = i
                notifyDataSetChanged()
            }
        }

    init {
        imageFolders = if (folders != null && folders.size > 0) folders else ArrayList()
        mImageSize = Utils.getImageItemWidth(mActivity)
        this.imageDataSource = imageDataSource
    }


    override fun getCount(): Int {
        return imageFolders?.size ?: 0
    }


    override fun getItem(position: Int): Fragment {

        val demoFragment =  GalleryFragment(mActivity, imageFolders)
        val bundle = Bundle()
        imageFolders!![position].name


        bundle.putInt("Selected_position", position)
        if (demoFragment != null) {
            demoFragment.setArguments(bundle)
        }

        return demoFragment!!
    }

    override  fun getPageTitle(position: Int): CharSequence {
        return imageFolders!![position].name.toString()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }


}
