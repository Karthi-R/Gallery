package com.custom.photoView.photoTapListener

import android.view.MotionEvent

import com.custom.photoView.photoTapListener.OnGestureListener

interface GestureDetector {

    abstract fun onTouchEvent(ev: MotionEvent): Boolean

    abstract fun isScaling(): Boolean

    abstract fun isDragging(): Boolean

    abstract fun setOnGestureListener(listener: OnGestureListener)

}
