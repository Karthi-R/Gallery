package com.custom.photoView.photoTapListener

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.widget.ImageView

import androidx.appcompat.widget.AppCompatImageView

class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attr, defStyle), IPhotoView {

    private var mAttacher: PhotoViewAttacher? = null

    private var mPendingScaleType: ImageView.ScaleType? = null

    init {
        super.setScaleType(ImageView.ScaleType.MATRIX)
        init()
    }

    protected fun init() {
        if (null == mAttacher || null == mAttacher!!.imageView) {
            mAttacher = PhotoViewAttacher(this)
        }

        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType!!)
            mPendingScaleType = null
        }
    }

    override fun setRotationTo(rotationDegree: Float) {
        mAttacher!!.setRotationTo(rotationDegree)
    }

    override fun setRotationBy(rotationDegree: Float) {
        mAttacher!!.setRotationBy(rotationDegree)
    }

    override fun canZoom(): Boolean {
        return mAttacher!!.canZoom()
    }

    override fun getDisplayRect(): RectF {
        return mAttacher!!.getDisplayRect()
    }

    override fun getDisplayMatrix(matrix: Matrix) {
        mAttacher!!.getDisplayMatrix(matrix)
    }

    override fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
        return mAttacher!!.setDisplayMatrix(finalRectangle)
    }

    override fun getMinimumScale(): Float {
        return mAttacher!!.getMinimumScale()
    }

    override fun getMediumScale(): Float {
        return mAttacher!!.getMediumScale()
    }

    override fun getMaximumScale(): Float {
        return mAttacher!!.getMaximumScale()
    }

    override fun getScale(): Float {
        return mAttacher!!.getScale()
    }

    override fun getScaleType(): ImageView.ScaleType {
        return mAttacher!!.getScaleType()
    }

    override fun getImageMatrix(): Matrix {
        return mAttacher!!.imageMatrix
    }

    override fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAttacher!!.setAllowParentInterceptOnEdge(allow)
    }

    override fun setMinimumScale(minimumScale: Float) {
        mAttacher!!.setMinimumScale(minimumScale)
    }

    override fun setMediumScale(mediumScale: Float) {
        mAttacher!!.setMediumScale(mediumScale)
    }

    override fun setMaximumScale(maximumScale: Float) {
        mAttacher!!.setMaximumScale(maximumScale)
    }

    override fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        mAttacher!!.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    override// setImageBitmap calls through to this method
    fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (null != mAttacher) {
            mAttacher!!.update()
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (null != mAttacher) {
            mAttacher!!.update()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        if (null != mAttacher) {
            mAttacher!!.update()
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (null != mAttacher) {
            mAttacher!!.update()
        }
        return changed
    }

    override fun setOnMatrixChangeListener(listener: PhotoViewAttacher.OnMatrixChangedListener) {
        mAttacher!!.setOnMatrixChangeListener(listener)
    }

/*
    fun OnLongClickListener?.setOnLongClickListener() {
        mAttacher!!.setOnLongClickListener(this)
    }
*/

    /*
    @Override
    public void setOnMatrixChangeListener(PhotoViewAttacher.OnMatrixChangedListener listener) {

    }
*/

    /*
    @Override
    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener) {

    }
*/

    /*
    @Override
    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {

    }
*/

    override fun setOnPhotoTapListener(listener: PhotoViewAttacher.OnPhotoTapListener) {
        mAttacher!!.setOnPhotoTapListener(listener)
    }

    override fun setOnViewTapListener(listener: PhotoViewAttacher.OnViewTapListener) {
        mAttacher!!.setOnViewTapListener(listener)
    }

    override fun setScale(scale: Float) {
        mAttacher!!.setScale(scale)
    }

    override fun setScale(scale: Float, animate: Boolean) {
        mAttacher!!.setScale(scale, animate)
    }

    override fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        mAttacher!!.setScale(scale, focalX, focalY, animate)
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (null != mAttacher) {
            mAttacher!!.setScaleType(scaleType)
        } else {
            mPendingScaleType = scaleType
        }
    }

    override fun setZoomable(zoomable: Boolean) {
        mAttacher!!.setZoomable(zoomable)
    }

    override fun getVisibleRectangleBitmap(): Bitmap {
        return mAttacher!!.getVisibleRectangleBitmap()
    }

    override fun setZoomTransitionDuration(milliseconds: Int) {
        mAttacher!!.setZoomTransitionDuration(milliseconds)
    }

    override fun getIPhotoViewImplementation(): IPhotoView {
        return this!!.mAttacher!!
    }

    override fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener) {
        mAttacher!!.setOnDoubleTapListener(newOnDoubleTapListener)
    }

    override fun setOnScaleChangeListener(onScaleChangeListener: PhotoViewAttacher.OnScaleChangeListener) {
        mAttacher!!.setOnScaleChangeListener(onScaleChangeListener)
    }

    override fun setOnSingleFlingListener(onSingleFlingListener: PhotoViewAttacher.OnSingleFlingListener) {
        mAttacher!!.setOnSingleFlingListener(onSingleFlingListener)
    }

    override fun onDetachedFromWindow() {
        mAttacher!!.cleanup()
        mAttacher = null
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        init()
        super.onAttachedToWindow()
    }
}
