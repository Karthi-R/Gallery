package com.custom.photoView.photoTapListener

import android.annotation.TargetApi
import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

import com.custom.photoView.photoTapListener.EclairGestureDetector

@TargetApi(8)
class FroyoGestureDetector(context: Context) : EclairGestureDetector(context) {

    protected var mDetector: ScaleGestureDetector

    init {
        val mScaleListener = object : ScaleGestureDetector.OnScaleGestureListener {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor

                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor))
                    return false

                mListener.onScale(scaleFactor,
                        detector.focusX, detector.focusY)
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // NO-OP
            }
        }
        mDetector = ScaleGestureDetector(context, mScaleListener)
    }

    override fun isScaling(): Boolean {
        return mDetector.isInProgress
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            mDetector.onTouchEvent(ev)
            return super.onTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            return true
        }

    }
}
