//package com.custom.photoView.photoGesture
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.graphics.Matrix.ScaleToFit
//import android.graphics.RectF
//import android.graphics.drawable.Drawable
//import android.util.Log
//import android.view.GestureDetector
//import android.view.MotionEvent
//import android.view.View
//import android.view.View.OnLongClickListener
//import android.view.ViewParent
//import android.view.ViewTreeObserver
//import android.view.animation.AccelerateDecelerateInterpolator
//import android.view.animation.Interpolator
//import android.widget.ImageView
//import android.widget.ImageView.ScaleType
//import androidx.core.view.MotionEventCompat
//
//import java.lang.ref.WeakReference
//
//import com.custom.photoView.photoGesture.OnGestureListener
//import com.custom.photoView.photoGesture.VersionedGestureDetector
////import uk.co.senab.photoview.log.LogManager;
//import com.custom.photoView.photoGesture.ScrollerProxy
//
//
//import com.custom.photoView.photoGesture.Compat
//
//import android.view.MotionEvent.ACTION_CANCEL
//import android.view.MotionEvent.ACTION_DOWN
//import android.view.MotionEvent.ACTION_UP//import uk.co.senab.photoview.log.LogManager;
//
//class PhotoViewAttacher @JvmOverloads constructor(imageView: ImageView, zoomable: Boolean = true) : IPhotoView, View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
//    override val mediumScale: Float
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override var maximumScale: Float
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//        set(value) {}
//    override var scale: Float
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//        set(value) {}
//
//    override fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun setOnViewTapListener(listener: OnViewTapListener) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
//    internal var ZOOM_DURATION = IPhotoView.DEFAULT_ZOOM_DURATION
//
//    private var mMinScale = IPhotoView.DEFAULT_MIN_SCALE
//    private var mMidScale = IPhotoView.DEFAULT_MID_SCALE
//    private var mMaxScale = IPhotoView.DEFAULT_MAX_SCALE
//
//    private var mAllowParentInterceptOnEdge = true
//    private var mBlockParentIntercept = false
//
//    private var mImageView: WeakReference<ImageView>? = null
//
//    // Gesture Detectors
//    private val mGestureDetector: GestureDetector?
//    private val mScaleDragDetector: com.custom.photoView.photoGesture.GestureDetector?
//
//    // These are set so we don't keep allocating them on the heap
//    private val mBaseMatrix = Matrix()
//    val imageMatrix = Matrix()
//    private val mSuppMatrix = Matrix()
//    private val mDisplayRect = RectF()
//    private val mMatrixValues = FloatArray(9)
//
//    // Listeners
//    private var mMatrixChangeListener: OnMatrixChangedListener? = null
//    private var mPhotoTapListener: OnPhotoTapListener? = null
//    private var mViewTapListener: OnViewTapListener? = null
//
//    private var mLongClickListener: OnLongClickListener? = null
//    private var mScaleChangeListener: OnScaleChangeListener? = null
//    private var mSingleFlingListener: OnSingleFlingListener? = null
//
//    private var mIvTop: Int = 0
//    private var mIvRight: Int = 0
//    private var mIvBottom: Int = 0
//    private var mIvLeft: Int = 0
//    private var mCurrentFlingRunnable: FlingRunnable? = null
//    private var mScrollEdge = EDGE_BOTH
//    private var mBaseRotation: Float = 0.toFloat()
//
//    private var mZoomEnabled: Boolean = false
//    private var mScaleType = ScaleType.FIT_CENTER
//
//    // If we don't have an ImageView, call cleanup()
//    //  LogManager.getLogger().i(LOG_TAG, "ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
//    val imageView: ImageView?
//        get() {
//            var imageView: ImageView? = null
//
//            if (null != mImageView) {
//                imageView = mImageView!!.get()
//            }
//            if (null == imageView) {
//                cleanup()
//            }
//
//            return imageView
//        }
//
//    private val drawMatrix: Matrix
//        get() {
//            imageMatrix.set(mBaseMatrix)
//            imageMatrix.postConcat(mSuppMatrix)
//            return imageMatrix
//        }
//
//    init {
//        mImageView = WeakReference(imageView)
//
//        imageView.isDrawingCacheEnabled = true
//        imageView.setOnTouchListener(this)
//
//        val observer = imageView.viewTreeObserver
//        observer?.addOnGlobalLayoutListener(this)
//
//        // Make sure we using MATRIX Scale Type
//        setImageViewScaleTypeMatrix(imageView)
//
//        run{
//            if (imageView.isInEditMode) {
//                return@run
//            }
//
//        }
//        // Create Gesture Detectors...
//        mScaleDragDetector = VersionedGestureDetector.newInstance(
//                imageView.context, this)
//
//        mGestureDetector = GestureDetector(imageView.context,
//                object : GestureDetector.SimpleOnGestureListener() {
//
//                    // forward long click listener
//                    override fun onLongPress(e: MotionEvent) {
//                        if (null != mLongClickListener) {
//                            mLongClickListener!!.onLongClick(imageView)
//                        }
//                    }
//
//                    override fun onFling(e1: MotionEvent, e2: MotionEvent,
//                                         velocityX: Float, velocityY: Float): Boolean {
//                        if (mSingleFlingListener != null) {
//                            if (scale > IPhotoView.DEFAULT_MIN_SCALE) {
//                                return false
//                            }
//
//                            return if (MotionEventCompat.getPointerCount(e1) > SINGLE_TOUCH || MotionEventCompat.getPointerCount(e2) > SINGLE_TOUCH) {
//                                false
//                            } else mSingleFlingListener!!.onFling(e1, e2, velocityX, velocityY)
//
//                        }
//                        return false
//                    }
//                })
//
//        mGestureDetector.setOnDoubleTapListener(DefaultOnDoubleTapListener(this))
//        mBaseRotation = 0.0f
//
//        // Finally, update the UI so that we're zoomable
//        setZoomable(zoomable)
//    }
//
//    override fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener) {
//        if (newOnDoubleTapListener != null) {
//            this.mGestureDetector!!.setOnDoubleTapListener(newOnDoubleTapListener)
//        } else {
//            this.mGestureDetector!!.setOnDoubleTapListener(DefaultOnDoubleTapListener(this))
//        }
//    }
//
//    override fun setOnScaleChangeListener(onScaleChangeListener: OnScaleChangeListener) {
//        this.mScaleChangeListener = onScaleChangeListener
//    }
//
//    override fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener) {
//        this.mSingleFlingListener = onSingleFlingListener
//    }
//
//    override fun canZoom(): Boolean {
//        return mZoomEnabled
//    }
//
//    /**
//     * Clean-up the resources attached to this object. This needs to be called when the ImageView is
//     * no longer used. A good example is from [android.view.View. or
//     * from [android.app.Activity.. This is automatically called if you are using
//     * [].
//     */
//    fun cleanup() {
//        if (null == mImageView) {
//            return  // cleanup already done
//        }
//
//        val imageView = mImageView!!.get()
//
//        if (null != imageView) {
//            // Remove this as a global layout listener
//            val observer = imageView.viewTreeObserver
//            if (null != observer && observer.isAlive) {
//                observer.removeGlobalOnLayoutListener(this)
//            }
//
//            // Remove the ImageView's reference to this
//            imageView.setOnTouchListener(null)
//
//            // make sure a pending fling runnable won't be run
//            cancelFling()
//        }
//
//        mGestureDetector?.setOnDoubleTapListener(null)
//
//        // Clear listeners too
//        mMatrixChangeListener = null
///*
//        onPhotoTapListener = null
//        onViewTapListener = null
//*/
//
//        // Finally, clear ImageView
//        mImageView = null
//    }
//
//    override val displayRect: RectF
//        get() {
//            checkMatrixBounds()
//            return this!!.getDisplayRect(drawMatrix)!!
//        }
//
//    override fun setDisplayMatrix(finalMatrix: Matrix): Boolean {
//        if (finalMatrix == null) {
//            throw IllegalArgumentException("Matrix cannot be null")
//        }
//
//        val imageView = imageView ?: return false
//
//        if (null == imageView.drawable) {
//            return false
//        }
//
//        mSuppMatrix.set(finalMatrix)
//        setImageViewMatrix(drawMatrix)
//        checkMatrixBounds()
//
//        return true
//    }
//
//    fun setBaseRotation(degrees: Float) {
//        mBaseRotation = degrees % 360
//        update()
//        setRotationBy(mBaseRotation)
//        checkAndDisplayMatrix()
//    }
//
//    override fun setRotationTo(degrees: Float) {
//        mSuppMatrix.setRotate(degrees % 360)
//        checkAndDisplayMatrix()
//    }
//
//    override fun setRotationBy(degrees: Float) {
//        mSuppMatrix.postRotate(degrees % 360)
//        checkAndDisplayMatrix()
//    }
//
//    override val minimumScale: Float
//        get() {
//            return mMinScale
//        }
//
///*
//    override var mediumScale: Float
//        get() {
//            return mMidScale
//        }
//*/
//
///*
//    override val maximumScale: Float
//        get() {
//            return mMaxScale
//        }
//*/
//
///*
//    override var scale: Float
//        get() {
//            return Math.sqrt((Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble(), 2.0).toFloat() + Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble(), 2.0).toFloat()).toDouble()).toFloat()
//        }
//*/
//
//    override val scaleType: ScaleType
//        get() {
//            return mScaleType
//        }
//
//    override fun onDrag(dx: Float, dy: Float) {
//        if (mScaleDragDetector!!.isScaling) {
//            return  // Do not drag if we are already scaling
//        }
//
//        if (DEBUG) {
//            //   LogManager.getLogger().d(LOG_TAG, String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));
//        }
//
//        val imageView = imageView
//        mSuppMatrix.postTranslate(dx, dy)
//        checkAndDisplayMatrix()
//
//        /**
//         * Here we decide whether to let the ImageView's parent to start taking
//         * over the touch event.
//         *
//         * First we check whether this function is enabled. We never want the
//         * parent to take over if we're scaling. We then check the edge we're
//         * on, and the direction of the scroll (i.e. if we're pulling against
//         * the edge, aka 'overscrolling', let the parent take over).
//         */
//        val parent = imageView!!.parent
//        if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling && !mBlockParentIntercept) {
//            if (mScrollEdge == EDGE_BOTH
//                    || mScrollEdge == EDGE_LEFT && dx >= 1f
//                    || mScrollEdge == EDGE_RIGHT && dx <= -1f) {
//                parent?.requestDisallowInterceptTouchEvent(false)
//            }
//        } else {
//            parent?.requestDisallowInterceptTouchEvent(true)
//        }
//    }
//
//    override fun onFling(startX: Float, startY: Float, velocityX: Float,
//                         velocityY: Float) {
//        if (DEBUG) {
//            //    LogManager.getLogger().d(LOG_TAG, "onFling. sX: " + startX + " sY: " + startY + " Vx: " + velocityX + " Vy: " + velocityY);
//        }
//        val imageView = imageView
//        mCurrentFlingRunnable = FlingRunnable(imageView!!.context)
//        mCurrentFlingRunnable!!.fling(getImageViewWidth(imageView),
//                getImageViewHeight(imageView), velocityX.toInt(), velocityY.toInt())
//        imageView.post(mCurrentFlingRunnable)
//    }
//
//    override fun onGlobalLayout() {
//        val imageView = imageView
//
//        if (null != imageView) {
//            if (mZoomEnabled) {
//                val top = imageView.top
//                val right = imageView.right
//                val bottom = imageView.bottom
//                val left = imageView.left
//
//                /**
//                 * We need to check whether the ImageView's bounds have changed.
//                 * This would be easier if we targeted API 11+ as we could just use
//                 * View.OnLayoutChangeListener. Instead we have to replicate the
//                 * work, keeping track of the ImageView's bounds and then checking
//                 * if the values change.
//                 */
//                if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
//                        || right != mIvRight) {
//                    // Update our base matrix, as the bounds have changed
//                    updateBaseMatrix(imageView.drawable)
//
//                    // Update values as something has changed
//                    mIvTop = top
//                    mIvRight = right
//                    mIvBottom = bottom
//                    mIvLeft = left
//                }
//            } else {
//                updateBaseMatrix(imageView.drawable)
//            }
//        }
//    }
//
//    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
//        if (DEBUG) {
//            //            LogManager.getLogger().d(
//            //                    LOG_TAG,
//            //                    String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f",
//            //                            scaleFactor, focusX, focusY));
//        }
//
//        if ((scale < mMaxScale || scaleFactor < 1f) && (scale > mMinScale || scaleFactor > 1f)) {
//            if (null != mScaleChangeListener) {
//                mScaleChangeListener!!.onScaleChange(scaleFactor, focusX, focusY)
//            }
//            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
//            checkAndDisplayMatrix()
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouch(v: View, ev: MotionEvent): Boolean {
//        var handled = false
//
//        if (mZoomEnabled && hasDrawable(v as ImageView)) {
//            val parent = v.getParent()
//            when (ev.action) {
//                ACTION_DOWN -> {
//                    // First, disable the Parent from intercepting the touch
//                    // event
//                    if (null != parent) {
//                        parent.requestDisallowInterceptTouchEvent(true)
//                    } else {
//                        //                        LogManager.getLogger().i(LOG_TAG, "onTouch getParent() returned null");
//                    }
//
//                    // If we're flinging, and the user presses down, cancel
//                    // fling
//                    cancelFling()
//                }
//
//                ACTION_CANCEL, ACTION_UP ->
//                    // If the user has zoomed less than min scale, zoom back
//                    // to min scale
//                    if (scale < mMinScale) {
//                        val rect = displayRect
//                        if (null != rect) {
//                            v.post(AnimatedZoomRunnable(scale, mMinScale,
//                                    rect.centerX(), rect.centerY()))
//                            handled = true
//                        }
//                    }
//            }
//
//            // Try the Scale/Drag detector
//            if (null != mScaleDragDetector) {
//                val wasScaling = mScaleDragDetector.isScaling
//                val wasDragging = mScaleDragDetector.isDragging
//
//                handled = mScaleDragDetector.onTouchEvent(ev)
//
//                val didntScale = !wasScaling && !mScaleDragDetector.isScaling
//                val didntDrag = !wasDragging && !mScaleDragDetector.isDragging
//
//                mBlockParentIntercept = didntScale && didntDrag
//            }
//
//            // Check to see if the user double tapped
//            if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
//                handled = true
//            }
//
//        }
//
//        return handled
//    }
//
//    override fun setAllowParentInterceptOnEdge(allow: Boolean) {
//        mAllowParentInterceptOnEdge = allow
//    }
//
///*
//    override fun setMinimumScale(minimumScale: Float) {
//        checkZoomLevels(minimumScale, mMidScale, mMaxScale)
//        mMinScale = minimumScale
//    }
//*/
//
///*
//    override fun setMediumScale(mediumScale: Float) {
//        checkZoomLevels(mMinScale, mediumScale, mMaxScale)
//        mMidScale = mediumScale
//    }
//*/
//
//    override fun setMaximumScale(maximumScale: Float) {
//        checkZoomLevels(mMinScale, mMidScale, maximumScale)
//        mMaxScale = maximumScale
//    }
//
//    override fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
//        checkZoomLevels(minimumScale, mediumScale, maximumScale)
//        mMinScale = minimumScale
//        mMidScale = mediumScale
//        mMaxScale = maximumScale
//    }
//
//    override fun setOnLongClickListener(listener: OnLongClickListener) {
//        mLongClickListener = listener
//    }
//
//    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
//        mMatrixChangeListener = listener
//    }
//
//    override fun setScale(scale: Float) {
//        setScale(scale, false)
//    }
//
//    override fun setScale(scale: Float, animate: Boolean) {
//        val imageView = imageView
//
//        if (null != imageView) {
//            setScale(scale,
//                    (imageView.right / 2).toFloat(),
//                    (imageView.bottom / 2).toFloat(),
//                    animate)
//        }
//    }
//
//    override fun setScale(scale: Float, focalX: Float, focalY: Float,
//                          animate: Boolean) {
//        val imageView = imageView
//
//        if (null != imageView) {
//            // Check to see if the scale is within bounds
//            if (scale < mMinScale || scale > mMaxScale) {
//                //                LogManager
//                //                        .getLogger()
//                //                        .i(LOG_TAG,
//                //                                "Scale must be within the range of minScale and maxScale");
//                return
//            }
//
//            if (animate) {
//                imageView.post(AnimatedZoomRunnable(scale, scale,
//                        focalX, focalY))
//            } else {
//                mSuppMatrix.setScale(scale, scale, focalX, focalY)
//                checkAndDisplayMatrix()
//            }
//        }
//    }
//
//    /**
//     * Set the zoom interpolator
//     * @param interpolator the zoom interpolator
//     */
//    fun setZoomInterpolator(interpolator: Interpolator) {
//        mInterpolator = interpolator
//    }
//
//    override fun setScaleType(scaleType: ScaleType) {
//        if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
//            mScaleType = scaleType
//
//            // Finally update
//            update()
//        }
//    }
//
//    override fun setZoomable(zoomable: Boolean) {
//        mZoomEnabled = zoomable
//        update()
//    }
//
//    fun update() {
//        val imageView = imageView
//
//        if (null != imageView) {
//            if (mZoomEnabled) {
//                // Make sure we using MATRIX Scale Type
//                setImageViewScaleTypeMatrix(imageView)
//
//                // Update the base matrix using the current drawable
//                updateBaseMatrix(imageView.drawable)
//            } else {
//                // Reset the Matrix...
//                resetMatrix()
//            }
//        }
//    }
//
//    /**
//     * Get the display matrix
//     * @param matrix target matrix to copy to
//     */
//    override fun getDisplayMatrix(matrix: Matrix) {
//        matrix.set(drawMatrix)
//    }
//
//    /**
//     * Get the current support matrix
//     */
//    fun getSuppMatrix(matrix: Matrix) {
//        matrix.set(mSuppMatrix)
//    }
//
//    private fun cancelFling() {
//        if (null != mCurrentFlingRunnable) {
//            mCurrentFlingRunnable!!.cancelFling()
//            mCurrentFlingRunnable = null
//        }
//    }
//
//    /**
//     * Helper method that simply checks the Matrix, and then displays the result
//     */
//    private fun checkAndDisplayMatrix() {
//        if (checkMatrixBounds()) {
//            setImageViewMatrix(drawMatrix)
//        }
//    }
//
//    private fun checkImageViewScaleType() {
//        val imageView = imageView
//
//        /**
//         * PhotoView's getScaleType() will just divert to this.getScaleType() so
//         * only call if we're not attached to a PhotoView.
//         */
//        if (null != imageView && imageView !is IPhotoView) {
//            if (ScaleType.MATRIX != imageView.scaleType) {
//                throw IllegalStateException(
//                        "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher. You should call setScaleType on the PhotoViewAttacher instead of on the ImageView")
//            }
//        }
//    }
//
//    private fun checkMatrixBounds(): Boolean {
//        val imageView = imageView ?: return false
//
//        val rect = getDisplayRect(drawMatrix) ?: return false
//
//        val height = rect.height()
//        val width = rect.width()
//        var deltaX = 0f
//        var deltaY = 0f
//
//        val viewHeight = getImageViewHeight(imageView)
//        if (height <= viewHeight) {
//            when (mScaleType) {
//                ImageView.ScaleType.FIT_START -> deltaY = -rect.top
//                ImageView.ScaleType.FIT_END -> deltaY = viewHeight.toFloat() - height - rect.top
//                else -> deltaY = (viewHeight - height) / 2 - rect.top
//            }
//        } else if (rect.top > 0) {
//            deltaY = -rect.top
//        } else if (rect.bottom < viewHeight) {
//            deltaY = viewHeight - rect.bottom
//        }
//
//        val viewWidth = getImageViewWidth(imageView)
//        if (width <= viewWidth) {
//            when (mScaleType) {
//                ImageView.ScaleType.FIT_START -> deltaX = -rect.left
//                ImageView.ScaleType.FIT_END -> deltaX = viewWidth.toFloat() - width - rect.left
//                else -> deltaX = (viewWidth - width) / 2 - rect.left
//            }
//            mScrollEdge = EDGE_BOTH
//        } else if (rect.left > 0) {
//            mScrollEdge = EDGE_LEFT
//            deltaX = -rect.left
//        } else if (rect.right < viewWidth) {
//            deltaX = viewWidth - rect.right
//            mScrollEdge = EDGE_RIGHT
//        } else {
//            mScrollEdge = EDGE_NONE
//        }
//
//        // Finally actually translate the matrix
//        mSuppMatrix.postTranslate(deltaX, deltaY)
//        return true
//    }
//
//    /**
//     * Helper method that maps the supplied Matrix to the current Drawable
//     *
//     * @param matrix - Matrix to map Drawable against
//     * @return RectF - Displayed Rectangle
//     */
//    private fun getDisplayRect(matrix: Matrix): RectF? {
//        val imageView = imageView
//
//        if (null != imageView) {
//            val d = imageView.drawable
//            if (null != d) {
//                mDisplayRect.set(0f, 0f, d.intrinsicWidth.toFloat(),
//                        d.intrinsicHeight.toFloat())
//                matrix.mapRect(mDisplayRect)
//                return mDisplayRect
//            }
//        }
//        return null
//    }
//
//    override val visibleRectangleBitmap: Bitmap
//        get() {
//            val imageView = imageView
//            return imageView?.drawingCache!!
//        }
//
//    override fun setZoomTransitionDuration(milliseconds: Int) {
//        var milliseconds = milliseconds
//        if (milliseconds < 0)
//            milliseconds = IPhotoView.DEFAULT_ZOOM_DURATION
//        this.ZOOM_DURATION = milliseconds
//    }
//
//    override val iPhotoViewImplementation: IPhotoView
//        get() {
//            return this
//        }
//
//    /**
//     * Helper method that 'unpacks' a Matrix and returns the required value
//     *
//     * @param matrix     - Matrix to unpack
//     * @param whichValue - Which value from Matrix.M* to return
//     * @return float - returned value
//     */
//    private fun getValue(matrix: Matrix, whichValue: Int): Float {
//        matrix.getValues(mMatrixValues)
//        return mMatrixValues[whichValue]
//    }
//
//    /**
//     * Resets the Matrix back to FIT_CENTER, and then displays it.s
//     */
//    private fun resetMatrix() {
//        mSuppMatrix.reset()
//        setRotationBy(mBaseRotation)
//        setImageViewMatrix(drawMatrix)
//        checkMatrixBounds()
//    }
//
//    private fun setImageViewMatrix(matrix: Matrix) {
//        val imageView = imageView
//        if (null != imageView) {
//
//            checkImageViewScaleType()
//            imageView.imageMatrix = matrix
//
//            // Call MatrixChangedListener if needed
//            if (null != mMatrixChangeListener) {
//                val displayRect = getDisplayRect(matrix)
//                if (null != displayRect) {
//                    mMatrixChangeListener!!.onMatrixChanged(displayRect)
//                }
//            }
//        }
//    }
//
//    /**
//     * Calculate Matrix for FIT_CENTER
//     *
//     * @param d - Drawable being displayed
//     */
//    private fun updateBaseMatrix(d: Drawable?) {
//        val imageView = imageView
//        if (null == imageView || null == d) {
//            return
//        }
//
//        val viewWidth = getImageViewWidth(imageView).toFloat()
//        val viewHeight = getImageViewHeight(imageView).toFloat()
//        val drawableWidth = d.intrinsicWidth
//        val drawableHeight = d.intrinsicHeight
//
//        mBaseMatrix.reset()
//
//        val widthScale = viewWidth / drawableWidth
//        val heightScale = viewHeight / drawableHeight
//
//        if (mScaleType == ScaleType.CENTER) {
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2f,
//                    (viewHeight - drawableHeight) / 2f)
//
//        } else if (mScaleType == ScaleType.CENTER_CROP) {
//            val scale = Math.max(widthScale, heightScale)
//            mBaseMatrix.postScale(scale, scale)
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
//                    (viewHeight - drawableHeight * scale) / 2f)
//
//        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
//            val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
//            mBaseMatrix.postScale(scale, scale)
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
//                    (viewHeight - drawableHeight * scale) / 2f)
//
//        } else {
//            var mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
//            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)
//
//            if (mBaseRotation.toInt() % 180 != 0) {
//                mTempSrc = RectF(0f, 0f, drawableHeight.toFloat(), drawableWidth.toFloat())
//            }
//
//            when (mScaleType) {
//                ImageView.ScaleType.FIT_CENTER -> mBaseMatrix
//                        .setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER)
//
//                ImageView.ScaleType.FIT_START -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START)
//
//                ImageView.ScaleType.FIT_END -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END)
//
//                ImageView.ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL)
//
//                else -> {
//                }
//            }
//        }
//
//        resetMatrix()
//    }
//
//    private fun getImageViewWidth(imageView: ImageView?): Int {
//        return if (null == imageView) 0 else imageView.width - imageView.paddingLeft - imageView.paddingRight
//    }
//
//    private fun getImageViewHeight(imageView: ImageView?): Int {
//        return if (null == imageView) 0 else imageView.height - imageView.paddingTop - imageView.paddingBottom
//    }
//
//    /**
//     * Interface definition for a callback to be invoked when the internal Matrix has changed for
//     * this View.
//     *
//     * @author Chris Banes
//     */
//    interface OnMatrixChangedListener {
//        /**
//         * Callback for when the Matrix displaying the Drawable has changed. This could be because
//         * the View's bounds have changed, or the user has zoomed.
//         *
//         * @param rect - Rectangle displaying the Drawable's new bounds.
//         */
//        fun onMatrixChanged(rect: RectF)
//    }
//
//    /**
//     * Interface definition for callback to be invoked when attached ImageView scale changes
//     *
//     * @author Marek Sebera
//     */
//    interface OnScaleChangeListener {
//        /**
//         * Callback for when the scale changes
//         *
//         * @param scaleFactor the scale factor (less than 1 for zoom out, greater than 1 for zoom in)
//         * @param focusX      focal point X position
//         * @param focusY      focal point Y position
//         */
//        fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float)
//    }
//
//    /**
//     * Interface definition for a callback to be invoked when the Photo is tapped with a single
//     * tap.
//     *
//     * @author Chris Banes
//     */
//    interface OnPhotoTapListener {
//
//        /**
//         * A callback to receive where the user taps on a photo. You will only receive a callback if
//         * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
//         *
//         * @param view - View the user tapped.
//         * @param x    - where the user tapped from the of the Drawable, as percentage of the
//         * Drawable width.
//         * @param y    - where the user tapped from the top of the Drawable, as percentage of the
//         * Drawable height.
//         */
//        fun onPhotoTap(view: View, x: Float, y: Float)
//
//        /**
//         * A simple callback where out of photo happened;
//         */
//        fun onOutsidePhotoTap()
//    }
//
//    /**
//     * Interface definition for a callback to be invoked when the ImageView is tapped with a single
//     * tap.
//     *
//     * @author Chris Banes
//     */
//    interface OnViewTapListener {
//
//        /**
//         * A callback to receive where the user taps on a ImageView. You will receive a callback if
//         * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.
//         *
//         * @param view - View the user tapped.
//         * @param x    - where the user tapped from the left of the View.
//         * @param y    - where the user tapped from the top of the View.
//         */
//        fun onViewTap(view: View, x: Float, y: Float)
//    }
//
//    /**
//     * Interface definition for a callback to be invoked when the ImageView is fling with a single
//     * touch
//     *
//     * @author tonyjs
//     */
//    interface OnSingleFlingListener {
//
//        /**
//         * A callback to receive where the user flings on a ImageView. You will receive a callback if
//         * the user flings anywhere on the view.
//         *
//         * @param e1        - MotionEvent the user first touch.
//         * @param e2        - MotionEvent the user last touch.
//         * @param velocityX - distance of user's horizontal fling.
//         * @param velocityY - distance of user's vertical fling.
//         */
//        fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean
//    }
//
//    private inner class AnimatedZoomRunnable(private val mZoomStart: Float, private val mZoomEnd: Float,
//                                             private val mFocalX: Float, private val mFocalY: Float) : Runnable {
//        private val mStartTime: Long
//
//        init {
//            mStartTime = System.currentTimeMillis()
//        }
//
//        override fun run() {
//            val imageView = imageView ?: return
//
//            val t = interpolate()
//            val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
//            val deltaScale = scale / scale
//
//            onScale(deltaScale, mFocalX, mFocalY)
//
//            // We haven't hit our target scale yet, so post ourselves again
//            if (t < 1f) {
//                Compat.postOnAnimation(imageView, this)
//            }
//        }
//
//        private fun interpolate(): Float {
//            var t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION
//            t = Math.min(1f, t)
//            t = mInterpolator.getInterpolation(t)
//            return t
//        }
//    }
//
//    private inner class FlingRunnable(context: Context) : Runnable {
//
//        private val mScroller: ScrollerProxy
//        private var mCurrentX: Int = 0
//        private var mCurrentY: Int = 0
//
//        init {
//            mScroller = ScrollerProxy.getScroller(context)
//        }
//
//        fun cancelFling() {
//            if (DEBUG) {
//                //                LogManager.getLogger().d(LOG_TAG, "Cancel Fling");
//            }
//            mScroller.forceFinished(true)
//        }
//
//        fun fling(viewWidth: Int, viewHeight: Int, velocityX: Int,
//                  velocityY: Int) {
//            val rect = displayRect ?: return
//
//            val startX = Math.round(-rect.left)
//            val minX: Int
//            val maxX: Int
//            val minY: Int
//            val maxY: Int
//
//            if (viewWidth < rect.width()) {
//                minX = 0
//                maxX = Math.round(rect.width() - viewWidth)
//            } else {
//                maxX = startX
//                minX = maxX
//            }
//
//            val startY = Math.round(-rect.top)
//            if (viewHeight < rect.height()) {
//                minY = 0
//                maxY = Math.round(rect.height() - viewHeight)
//            } else {
//                maxY = startY
//                minY = maxY
//            }
//
//            mCurrentX = startX
//            mCurrentY = startY
//
//            if (DEBUG) {
//                //                LogManager.getLogger().d(
//                //                        LOG_TAG,
//                //                        "fling. StartX:" + startX + " StartY:" + startY
//                //                                + " MaxX:" + maxX + " MaxY:" + maxY);
//            }
//
//            // If we actually can move, fling the scroller
//            if (startX != maxX || startY != maxY) {
//                mScroller.fling(startX, startY, velocityX, velocityY, minX,
//                        maxX, minY, maxY, 0, 0)
//            }
//        }
//
//        override fun run() {
//            if (mScroller.isFinished) {
//                return  // remaining post that should not be handled
//            }
//
//            val imageView = imageView
//            if (null != imageView && mScroller.computeScrollOffset()) {
//
//                val newX = mScroller.currX
//                val newY = mScroller.currY
//
//                if (DEBUG) {
//                    //                    LogManager.getLogger().d(
//                    //                            LOG_TAG,
//                    //                            "fling run(). CurrentX:" + mCurrentX + " CurrentY:"
//                    //                                    + mCurrentY + " NewX:" + newX + " NewY:"
//                    //                                    + newY);
//                }
//
//                mSuppMatrix.postTranslate((mCurrentX - newX).toFloat(), (mCurrentY - newY).toFloat())
//                setImageViewMatrix(drawMatrix)
//
//                mCurrentX = newX
//                mCurrentY = newY
//
//                // Post On animation
//                Compat.postOnAnimation(imageView, this)
//            }
//        }
//    }
//
//    companion object {
//
//        private val LOG_TAG = "PhotoViewAttacher"
//
//        // let debug flag be dynamic, but still Proguard can be used to remove from
//        // release builds
//        private val DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG)
//
//        internal val EDGE_NONE = -1
//        internal val EDGE_LEFT = 0
//        internal val EDGE_RIGHT = 1
//        internal val EDGE_BOTH = 2
//
//        internal var SINGLE_TOUCH = 1
//
//        private fun checkZoomLevels(minZoom: Float, midZoom: Float,
//                                    maxZoom: Float) {
//            if (minZoom >= midZoom) {
//                throw IllegalArgumentException(
//                        "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value")
//            } else if (midZoom >= maxZoom) {
//                throw IllegalArgumentException(
//                        "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value")
//            }
//        }
//
//        /**
//         * @return true if the ImageView exists, and its Drawable exists
//         */
//        private fun hasDrawable(imageView: ImageView?): Boolean {
//            return null != imageView && null != imageView.drawable
//        }
//
//        /**
//         * @return true if the ScaleType is supported.
//         */
//        private fun isSupportedScaleType(scaleType: ScaleType?): Boolean {
//            if (null == scaleType) {
//                return false
//            }
//
//            when (scaleType) {
//                ImageView.ScaleType.MATRIX -> throw IllegalArgumentException(scaleType.name + " is not supported in PhotoView")
//
//                else -> return true
//            }
//        }
//
//        /**
//         * Sets the ImageView's ScaleType to Matrix.
//         */
//        private fun setImageViewScaleTypeMatrix(imageView: ImageView?) {
//            /**
//             * PhotoView sets its own ScaleType to Matrix, then diverts all calls
//             * setScaleType to this.setScaleType automatically.
//             */
//            if (null != imageView && imageView !is IPhotoView) {
//                if (ScaleType.MATRIX != imageView.scaleType) {
//                    imageView.scaleType = ScaleType.MATRIX
//                }
//            }
//        }
//    }
//}
