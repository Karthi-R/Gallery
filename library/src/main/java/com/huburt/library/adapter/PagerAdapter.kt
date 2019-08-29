package com.huburt.library.adapter

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.huburt.library.ImageDataSource
import com.huburt.library.bean.ImageFolder
import com.huburt.library.ui.ImageGridFragment
import com.huburt.library.util.Utils
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

      //  val demoFragment = imageDataSource?.let { ImageGridFragment(mActivity, it) }
        val demoFragment =  ImageGridFragment(mActivity, imageFolders)

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
