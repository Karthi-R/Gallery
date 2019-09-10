package com.custom.photoView.photoGesture

import android.view.MotionEvent

import com.custom.photoView.photoGesture.OnGestureListener

interface GestureDetector {

    val isScaling: Boolean

    val isDragging: Boolean

    fun onTouchEvent(ev: MotionEvent): Boolean

    fun setOnGestureListener(listener: OnGestureListener)

}
