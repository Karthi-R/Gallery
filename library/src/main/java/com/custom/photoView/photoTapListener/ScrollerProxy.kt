package com.custom.photoView.photoTapListener

import android.content.Context
import android.os.Build

import com.custom.photoView.photoTapListener.GingerScroller
import com.custom.photoView.photoTapListener.IcsScroller
import com.custom.photoView.photoTapListener.PreGingerScroller

abstract class ScrollerProxy {

    companion object{
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


    abstract fun computeScrollOffset(): Boolean

    abstract fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int,
                       maxY: Int, overX: Int, overY: Int)

    abstract fun forceFinished(finished: Boolean)

    abstract fun isFinished(): Boolean

    abstract fun getCurrX(): Int

    abstract fun getCurrY(): Int


}
