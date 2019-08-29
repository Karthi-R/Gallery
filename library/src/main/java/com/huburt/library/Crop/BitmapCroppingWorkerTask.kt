// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.huburt.library.Crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask

import java.lang.ref.WeakReference

/** Task to crop bitmap asynchronously from the UI thread.  */
internal class BitmapCroppingWorkerTask : AsyncTask<Void, Void, BitmapCroppingWorkerTask.Result> {

    // region: Fields and Consts

    /** Use a WeakReference to ensure the ImageView can be garbage collected  */
    private val mCropImageViewReference: WeakReference<CropImageView>

    /** the bitmap to crop  */
    private val mBitmap: Bitmap?

    /** The Android URI of the image to load  */
    /** The Android URI that this task is currently loading.  */
    val uri: Uri?

    /** The context of the crop image view widget used for loading of bitmap by Android URI  */
    private val mContext: Context

    /** Required cropping 4 points (x0,y0,x1,y1,x2,y2,x3,y3)  */
    private val mCropPoints: FloatArray

    /** Degrees the image was rotated after loading  */
    private val mDegreesRotated: Int

    /** the original width of the image to be cropped (for image loaded from URI)  */
    private val mOrgWidth: Int

    /** the original height of the image to be cropped (for image loaded from URI)  */
    private val mOrgHeight: Int

    /** is there is fixed aspect ratio for the crop rectangle  */
    private val mFixAspectRatio: Boolean

    /** the X aspect ration of the crop rectangle  */
    private val mAspectRatioX: Int

    /** the Y aspect ration of the crop rectangle  */
    private val mAspectRatioY: Int

    /** required width of the cropping image  */
    private val mReqWidth: Int

    /** required height of the cropping image  */
    private val mReqHeight: Int

    /** is the image flipped horizontally  */
    private val mFlipHorizontally: Boolean

    /** is the image flipped vertically  */
    private val mFlipVertically: Boolean

    /** The option to handle requested width/height  */
    private val mReqSizeOptions: CropImageView.RequestSizeOptions

    /** the Android Uri to save the cropped image to  */
    private val mSaveUri: Uri?

    /** the compression format to use when writing the image  */
    private val mSaveCompressFormat: Bitmap.CompressFormat

    /** the quality (if applicable) to use when writing the image (0 - 100)  */
    private val mSaveCompressQuality: Int
    // endregion

    constructor(
            cropImageView: CropImageView,
            bitmap: Bitmap,
            cropPoints: FloatArray,
            degreesRotated: Int,
            fixAspectRatio: Boolean,
            aspectRatioX: Int,
            aspectRatioY: Int,
            reqWidth: Int,
            reqHeight: Int,
            flipHorizontally: Boolean,
            flipVertically: Boolean,
            options: CropImageView.RequestSizeOptions,
            saveUri: Uri,
            saveCompressFormat: Bitmap.CompressFormat,
            saveCompressQuality: Int) {

        mCropImageViewReference = WeakReference<CropImageView>(cropImageView)
        mContext = cropImageView.getContext()
        mBitmap = bitmap
        mCropPoints = cropPoints
        uri = null
        mDegreesRotated = degreesRotated
        mFixAspectRatio = fixAspectRatio
        mAspectRatioX = aspectRatioX
        mAspectRatioY = aspectRatioY
        mReqWidth = reqWidth
        mReqHeight = reqHeight
        mFlipHorizontally = flipHorizontally
        mFlipVertically = flipVertically
        mReqSizeOptions = options
        mSaveUri = saveUri
        mSaveCompressFormat = saveCompressFormat
        mSaveCompressQuality = saveCompressQuality
        mOrgWidth = 0
        mOrgHeight = 0
    }

    constructor(
            cropImageView: CropImageView,
            uri: Uri,
            cropPoints: FloatArray,
            degreesRotated: Int,
            orgWidth: Int,
            orgHeight: Int,
            fixAspectRatio: Boolean,
            aspectRatioX: Int,
            aspectRatioY: Int,
            reqWidth: Int,
            reqHeight: Int,
            flipHorizontally: Boolean,
            flipVertically: Boolean,
            options: CropImageView.RequestSizeOptions,
            saveUri: Uri,
            saveCompressFormat: Bitmap.CompressFormat,
            saveCompressQuality: Int) {

        mCropImageViewReference = WeakReference<CropImageView>(cropImageView)
        mContext = cropImageView.getContext()
        this.uri = uri
        mCropPoints = cropPoints
        mDegreesRotated = degreesRotated
        mFixAspectRatio = fixAspectRatio
        mAspectRatioX = aspectRatioX
        mAspectRatioY = aspectRatioY
        mOrgWidth = orgWidth
        mOrgHeight = orgHeight
        mReqWidth = reqWidth
        mReqHeight = reqHeight
        mFlipHorizontally = flipHorizontally
        mFlipVertically = flipVertically
        mReqSizeOptions = options
        mSaveUri = saveUri
        mSaveCompressFormat = saveCompressFormat
        mSaveCompressQuality = saveCompressQuality
        mBitmap = null
    }

    /**
     * Crop image in background.
     *
     * @param params ignored
     * @return the decoded bitmap data
     */
    override fun doInBackground(vararg params: Void): BitmapCroppingWorkerTask.Result? {
        try {
            if (!isCancelled) {

                val bitmapSampled: BitmapUtils.BitmapSampled
                if (uri != null) {
                    bitmapSampled = BitmapUtils.cropBitmap(
                            mContext,
                            uri,
                            mCropPoints,
                            mDegreesRotated,
                            mOrgWidth,
                            mOrgHeight,
                            mFixAspectRatio,
                            mAspectRatioX,
                            mAspectRatioY,
                            mReqWidth,
                            mReqHeight,
                            mFlipHorizontally,
                            mFlipVertically)
                } else if (mBitmap != null) {
                    bitmapSampled = BitmapUtils.cropBitmapObjectHandleOOM(
                            mBitmap,
                            mCropPoints,
                            mDegreesRotated,
                            mFixAspectRatio,
                            mAspectRatioX,
                            mAspectRatioY,
                            mFlipHorizontally,
                            mFlipVertically)!!
                } else {
                    return Result(null as Bitmap, 1)
                }

                val bitmap = bitmapSampled.bitmap?.let { BitmapUtils.resizeBitmap(it, mReqWidth, mReqHeight, mReqSizeOptions) }

                if (mSaveUri == null) {
                    return bitmap?.let { Result(it, bitmapSampled.sampleSize) }
                } else {
                    if (bitmap != null) {
                        BitmapUtils.writeBitmapToUri(
                                mContext, bitmap, mSaveUri, mSaveCompressFormat, mSaveCompressQuality)
                    }
                    if (bitmap != null) {
                        bitmap!!.recycle()
                    }
                    return Result(mSaveUri, bitmapSampled.sampleSize)
                }
            }
            return null
        } catch (e: Exception) {
            return Result(e, mSaveUri != null)
        }

    }

    /**
     * Once complete, see if ImageView is still around and set bitmap.
     *
     * @param result the result of bitmap cropping
     */
    override fun onPostExecute(result: Result?) {
        if (result != null) {
            var completeCalled = false
            if (!isCancelled) {
                val cropImageView = mCropImageViewReference.get()
                if (cropImageView != null) {
                    completeCalled = true
                    cropImageView!!.onImageCroppingAsyncComplete(result)
                }
            }
            if (!completeCalled && result.bitmap != null) {
                // fast release of unused bitmap
                result.bitmap.recycle()
            }
        }
    }

    // region: Inner class: Result

    /** The result of BitmapCroppingWorkerTask async loading.  */
    internal class Result {

        /** The cropped bitmap  */
        val bitmap: Bitmap?

        /** The saved cropped bitmap uri  */
        val uri: Uri?

        /** The error that occurred during async bitmap cropping.  */
        val error: Exception?

        /** is the cropping request was to get a bitmap or to save it to uri  */
        val isSave: Boolean

        /** sample size used creating the crop bitmap to lower its size  */
        val sampleSize: Int

        constructor(bitmap: Bitmap, sampleSize: Int) {
            this.bitmap = bitmap
            this.uri = null
            this.error = null
            this.isSave = false
            this.sampleSize = sampleSize
        }

        constructor(uri: Uri, sampleSize: Int) {
            this.bitmap = null
            this.uri = uri
            this.error = null
            this.isSave = true
            this.sampleSize = sampleSize
        }

        constructor(error: Exception, isSave: Boolean) {
            this.bitmap = null
            this.uri = null
            this.error = error
            this.isSave = isSave
            this.sampleSize = 1
        }
    }
    // endregion
}
