package com.custom.photoView.photoGesture


import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

import com.custom.photoView.photoGesture.GestureDetector
import com.custom.photoView.photoGesture.OnGestureListener

//import uk.co.senab.photoview.log.LogManager;

open class CupcakeGestureDetector(context: Context) : GestureDetector {

    protected lateinit var mListener: OnGestureListener
    internal var mLastTouchX: Float = 0.toFloat()
    internal var mLastTouchY: Float = 0.toFloat()
    internal val mTouchSlop: Float
    internal val mMinimumVelocity: Float

    private var mVelocityTracker: VelocityTracker? = null
    private var mIsDragging: Boolean = false

    override fun setOnGestureListener(listener: OnGestureListener) {
        this.mListener = listener
    }

    init {
        val configuration = ViewConfiguration
                .get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
    }

    internal open fun getActiveX(ev: MotionEvent): Float {
        return ev.x
    }

    internal open fun getActiveY(ev: MotionEvent): Float {
        return ev.y
    }

    override val isScaling: Boolean = false
    override val isDragging: Boolean = mIsDragging


/*
    override fun isScaling(): Boolean {
        return false
    }

    override fun isDragging(): Boolean {
        return mIsDragging
    }
*/

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.addMovement(ev)
                } else {
                    // LogManager.getLogger().i(LOG_TAG, "Velocity tracker is null");
                }

                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                mIsDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY

                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                }

                if (mIsDragging) {
                    mListener.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y

                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        mVelocityTracker!!.addMovement(ev)
                        mVelocityTracker!!.computeCurrentVelocity(1000)

                        val vX = mVelocityTracker!!.xVelocity
                        val vY = mVelocityTracker!!
                                .yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX,
                                    -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
        }

        return true
    }

    companion object {
        private val LOG_TAG = "CupcakeGestureDetector"
    }
}

