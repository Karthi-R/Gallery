package com.custom.photoView.photoGesture

import android.annotation.TargetApi
import android.content.Context

import com.custom.photoView.photoGesture.GingerScroller

@TargetApi(14)
class IcsScroller(context: Context) : GingerScroller(context) {

    override fun computeScrollOffset(): Boolean {
        return mScroller.computeScrollOffset()
    }

}
