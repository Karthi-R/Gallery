package com.custom.photoView.photoTapListener

import android.content.Context
import android.widget.Scroller

import com.custom.photoView.photoTapListener.ScrollerProxy

class PreGingerScroller(context: Context) : ScrollerProxy() {

    private var mScroller: Scroller

    init {
        mScroller = Scroller(context)
    }

    override fun computeScrollOffset(): Boolean {
        return mScroller.computeScrollOffset()
    }

    override fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int, maxY: Int,
                       overX: Int, overY: Int) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
    }

    override fun forceFinished(finished: Boolean) {
        mScroller.forceFinished(finished)
    }

    override fun isFinished(): Boolean {
        return mScroller.isFinished
    }

    override fun getCurrX(): Int {
        return mScroller.currX
    }

    override fun getCurrY(): Int {
        return mScroller.currY
    }
}