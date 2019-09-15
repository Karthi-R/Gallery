package com.custom.photoView.photoTapListener


import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView


interface IPhotoView {

    /**
     * Gets the Display Rectangle of the currently displayed Drawable. The Rectangle is relative to
     * this View and includes all scaling and translations.
     *
     * @return - RectF of Displayed Drawable
     */
    abstract fun getDisplayRect(): RectF
   // abstract fun getDisplayMatrix(matrix: Matrix)


    /**
     * @return The current minimum scale level. What this value represents depends on the current
     * [android.widget.ImageView.ScaleType].
     */
    /**
     * Sets the minimum scale level. What this value represents depends on the current [ ].
     *
     * @param minimumScale minimum allowed scale
     */
    abstract fun getMinimumScale(): Float

    /**
     * @return The current medium scale level. What this value represents depends on the current
     * [android.widget.ImageView.ScaleType].
     */
    /**
     * Sets the medium scale level. What this value represents depends on the current [android.widget.ImageView.ScaleType].
     *
     * @param mediumScale medium scale preset
     */
    abstract fun getMediumScale(): Float

    /**
     * @return The current maximum scale level. What this value represents depends on the current
     * [android.widget.ImageView.ScaleType].
     */
    /**
     * Sets the maximum scale level. What this value represents depends on the current [ ].
     *
     * @param maximumScale maximum allowed scale preset
     */
    abstract fun getMaximumScale(): Float

    /**
     * Returns the current scale value
     *
     * @return float - current scale value
     */
    /**
     * Changes the current scale to the specified value.
     *
     * @param scale - Value to scale to
     */
    abstract fun getScale(): Float

    /**
     * Return the current scale type in use by the ImageView.
     *
     * @return current ImageView.ScaleType
     */
    /**
     * Controls how the image should be resized or moved to match the size of the ImageView. Any
     * scaling or panning will happen within the confines of this [ ].
     *
     * @param scaleType - The desired scaling mode.
     */
    abstract fun getScaleType(): ImageView.ScaleType

    /**
     * Extracts currently visible area to Bitmap object, if there is no image loaded yet or the
     * ImageView is already destroyed, returns `null`
     *
     * @return currently visible area as bitmap or null
     */
    abstract fun getVisibleRectangleBitmap(): Bitmap

    /**
     * Will return instance of IPhotoView (eg. PhotoViewAttacher), can be used to provide better
     * integration
     *
     * @return IPhotoView implementation instance if available, null if not
     */
    abstract fun getIPhotoViewImplementation(): IPhotoView

    /**
     * Returns true if the PhotoView is set to allow zooming of Photos.
     *
     * @return true if the PhotoView allows zooming.
     */
    fun canZoom(): Boolean

    /**
     * Sets the Display Matrix of the currently displayed Drawable. The Rectangle is considered
     * relative to this View and includes all scaling and translations.
     *
     * @param finalMatrix target matrix to set PhotoView to
     * @return - true if rectangle was applied successfully
     */
    fun setDisplayMatrix(finalMatrix: Matrix): Boolean

    /**
     * Copies the Display Matrix of the currently displayed Drawable. The Rectangle is considered
     * relative to this View and includes all scaling and translations.
     *
     * @param matrix target matrix to copy to
     */
    fun getDisplayMatrix(matrix: Matrix)

    /**
     * Whether to allow the ImageView's parent to intercept the touch event when the photo is scroll
     * to it's horizontal edge.
     *
     * @param allow whether to allow intercepting by parent element or not
     */
    fun setAllowParentInterceptOnEdge(allow: Boolean)

    /**
     * Allows to set all three scale levels at once, so you don't run into problem with setting
     * medium/minimum scale before the maximum one
     *
     * @param minimumScale minimum allowed scale
     * @param mediumScale  medium allowed scale
     * @param maximumScale maximum allowed scale preset
     */
    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float)

    /**
     * Sets the minimum scale level. What this value represents depends on the current [ ].
     *
     * @param minimumScale minimum allowed scale
     */
    abstract fun setMinimumScale(minimumScale: Float)


    /**
     * Sets the medium scale level. What this value represents depends on the current [android.widget.ImageView.ScaleType].
     *
     * @param mediumScale medium scale preset
     */
    abstract fun setMediumScale(mediumScale: Float)

    /**
     * Sets the maximum scale level. What this value represents depends on the current [ ].
     *
     * @param maximumScale maximum allowed scale preset
     */
    abstract fun setMaximumScale(maximumScale: Float)

    /**
     * Register a callback to be invoked when the Photo displayed by this view is long-pressed.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnLongClickListener(listener: View.OnLongClickListener)

    /**
     * Register a callback to be invoked when the Matrix has changed for this View. An example would
     * be the user panning or scaling the Photo.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnMatrixChangeListener(listener: PhotoViewAttacher.OnMatrixChangedListener)

    /**
     * Register a callback to be invoked when the Photo displayed by this View is tapped with a
     * single tap.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnPhotoTapListener(listener: PhotoViewAttacher.OnPhotoTapListener)

    /**
     * Register a callback to be invoked when the View is tapped with a single tap.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnViewTapListener(listener: PhotoViewAttacher.OnViewTapListener)

    /**
     * Enables rotation via PhotoView internal functions.
     *
     * @param rotationDegree - Degree to rotate PhotoView to, should be in range 0 to 360
     */
    fun setRotationTo(rotationDegree: Float)

    /**
     * Enables rotation via PhotoView internal functions.
     *
     * @param rotationDegree - Degree to rotate PhotoView by, should be in range 0 to 360
     */
    fun setRotationBy(rotationDegree: Float)


    /**
     * Changes the current scale to the specified value.
     *
     * @param scale - Value to scale to
     */
    abstract fun setScale(scale: Float)

    /**
     * Changes the current scale to the specified value.
     *
     * @param scale   - Value to scale to
     * @param animate - Whether to animate the scale
     */
    abstract fun setScale(scale: Float, animate: Boolean)

    /**
     * Changes the current scale to the specified value, around the given focal point.
     *
     * @param scale   - Value to scale to
     * @param focalX  - X Focus Point
     * @param focalY  - Y Focus Point
     * @param animate - Whether to animate the scale
     */
    abstract fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean)


    /**
     * Controls how the image should be resized or moved to match the size of the ImageView. Any
     * scaling or panning will happen within the confines of this [ ].
     *
     * @param scaleType - The desired scaling mode.
     */
    abstract fun setScaleType(scaleType: ImageView.ScaleType)


    /**
     * Allows you to enable/disable the zoom functionality on the ImageView. When disable the
     * ImageView reverts to using the FIT_CENTER matrix.
     *
     * @param zoomable - Whether the zoom functionality is enabled.
     */
    fun setZoomable(zoomable: Boolean)

    /**
     * Allows to change zoom transition speed, default value is 200 (PhotoViewAttacher.DEFAULT_ZOOM_DURATION).
     * Will default to 200 if provided negative value
     *
     * @param milliseconds duration of zoom interpolation
     */
    fun setZoomTransitionDuration(milliseconds: Int)

    /**
     * Sets custom double tap listener, to intercept default given functions. To reset behavior to
     * default, you can just pass in "null" or public field of PhotoViewAttacher.defaultOnDoubleTapListener
     *
     * @param newOnDoubleTapListener custom OnDoubleTapListener to be set on ImageView
     */
    fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener)

    /**
     * Will report back about scale changes
     *
     * @param onScaleChangeListener OnScaleChangeListener instance
     */
    fun setOnScaleChangeListener(onScaleChangeListener: PhotoViewAttacher.OnScaleChangeListener)

    /**
     * Will report back about fling(single touch)
     *
     * @param onSingleFlingListener OnSingleFlingListener instance
     */
    fun setOnSingleFlingListener(onSingleFlingListener: PhotoViewAttacher.OnSingleFlingListener)

    companion object {

        val DEFAULT_MAX_SCALE = 3.0f
        val DEFAULT_MID_SCALE = 1.75f
        val DEFAULT_MIN_SCALE = 1.0f
        val DEFAULT_ZOOM_DURATION = 200
    }
}

