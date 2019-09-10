package com.custom.photoView.photoGesture

import android.annotation.TargetApi
import android.content.Context
import android.widget.OverScroller

import com.custom.photoView.photoGesture.ScrollerProxy

@TargetApi(9)
open class GingerScroller(context: Context) : ScrollerProxy() {

    protected val mScroller: OverScroller

    init {
        mScroller = OverScroller(context)
    }

    override fun computeScrollOffset(): Boolean {
        return mScroller.computeScrollOffset()
    }

    override fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int, maxY: Int,
                       overX: Int, overY: Int) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY)
    }

    override fun forceFinished(finished: Boolean) {
        mScroller.forceFinished(finished)
    }

    /*   override fun isFinished(): Boolean {
           return mScroller.isFinished
       }

       override fun getCurrX(): Int {
           return mScroller.currX
       }

       override fun getCurrY(): Int {
           return mScroller.currY
       }*/

    override val isFinished: Boolean = mScroller.isFinished
    override val currX: Int = mScroller.currX
    override val currY: Int = mScroller.currY

}