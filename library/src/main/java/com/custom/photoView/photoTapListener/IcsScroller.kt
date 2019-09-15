package com.custom.photoView.photoTapListener

import android.annotation.TargetApi
import android.content.Context

import com.custom.photoView.photoTapListener.GingerScroller

@TargetApi(14)
class IcsScroller(context: Context) : GingerScroller(context) {


    override fun computeScrollOffset(): Boolean {
        return mScroller.computeScrollOffset()
    }


}
