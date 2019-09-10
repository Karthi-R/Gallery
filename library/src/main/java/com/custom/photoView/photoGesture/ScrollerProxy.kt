package com.custom.photoView.photoGesture

import android.content.Context
import android.os.Build

import com.custom.photoView.photoGesture.GingerScroller
import com.custom.photoView.photoGesture.IcsScroller
import com.custom.photoView.photoGesture.PreGingerScroller

abstract class ScrollerProxy {

    abstract val isFinished: Boolean

    abstract val currX: Int

    abstract val currY: Int

    abstract fun computeScrollOffset(): Boolean

    abstract fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int,
                       maxY: Int, overX: Int, overY: Int)

    abstract fun forceFinished(finished: Boolean)

    companion object {

        fun getScroller(context: Context): ScrollerProxy {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                PreGingerScroller(context)
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                GingerScroller(context)
            } else {
                IcsScroller(context)
            }
        }
    }


}
