package com.custom.library.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import com.custom.library.CropView.CropImage
import com.custom.library.CropView.CropImageOptions
import com.custom.library.CropView.CropImageView
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.IOException
import android.widget.SeekBar

import com.custom.library.ui.ImageEditActivity.Companion.CROP_IMAGE_REQUEST_CODE
import com.custom.library.ui.ImagePreviewActivity.Companion.IMAGE_PREVIEW_REQUEST_CODE
import com.custom.library.util.FileUtil.getCropCacheFolder
import java.util.*
import kotlin.random.Random


class CropActivity : AppCompatActivity(),com.custom.library.CropView.CropImageView.OnSetImageUriCompleteListener, com.custom.library.CropView.CropImageView.OnCropImageCompleteListener {

    private var position = 0

    /** The crop image view library widget used in the activity  */
    private var mCropImageView: CropImageView? = null

    /** Persist URI image to crop URI if specific permissions are required  */
    private var mCropImageUri: Uri? = null

    /** the options that were set for the crop image  */
    private var mOptions: CropImageOptions? = null

    protected val outputUri: Uri
        get() {
            var outputUri = mOptions!!.outputUri
            if (outputUri == null || outputUri == Uri.EMPTY) {
                try {
                    val ext = if (mOptions!!.outputCompressFormat === Bitmap.CompressFormat.JPEG)
                        ".jpg"
                    else if (mOptions!!.outputCompressFormat === Bitmap.CompressFormat.PNG) ".png" else ".webp"
                    outputUri = Uri.fromFile(File.createTempFile(Random.nextInt().toString(), ".jpg", getCropCacheFolder(this)))

                } catch (e: IOException) {
                    throw RuntimeException("Failed to create temp file for output image", e)
                }

            }
            return outputUri!!
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.custom.library.R.layout.activity_crop)

        mCropImageView =  findViewById(com.custom.library.R.id.mCropImageView)

        cancel.setOnClickListener { finish() }

        btn_back.setOnClickListener { finish() }

        position = intent.getIntExtra("position",0)

        val bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)

        val path=intent.getStringExtra("Path")

        mCropImageView?.setImageUriAsync(Uri.fromFile(File(path)))

        btn_back.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            sb_value.visibility = View.GONE
            cropImage()
        }

        rotate_right.setOnClickListener {
            sb_value.visibility = View.GONE
            rotateImage(mOptions!!.rotationDegrees)
        }

        brightness.setOnClickListener {
            sb_value.visibility = View.VISIBLE
        }

        sb_value.progress = 125

        sb_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mCropImageView!!.changeBitmapContrastBrightness(progress / 100f, 1F)?.let { mCropImageView!!.setImageBitmap(it) }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })


        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            val title = if (mOptions != null &&
                    mOptions!!.activityTitle != null && mOptions!!.activityTitle.length > 0)
                mOptions!!.activityTitle
            else
                getResources().getString(com.custom.library.R.string.crop_image_activity_title)

        }
    }


    protected override fun onStart() {
        super.onStart()
        mCropImageView!!.setOnSetImageUriCompleteListener(this)
        mCropImageView!!.setOnCropImageCompleteListener(this)
    }

    protected override fun onStop() {
        super.onStop()
        mCropImageView!!.setOnSetImageUriCompleteListener(null)
        mCropImageView!!.setOnCropImageCompleteListener(null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }
    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception) {
        if (error == null) {
            if (mOptions!!.initialCropWindowRectangle != null) {
                mCropImageView!!.cropRect=(mOptions!!.initialCropWindowRectangle)
            }
            if (mOptions!!.initialRotation > -1) {
                mCropImageView!!.rotatedDegrees=(mOptions!!.initialRotation)
            }
        } else {
            setResult(null, error, 1)
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        setResult(result!!.uri, result.error, result.sampleSize)
    }


    /** Execute crop image and save the result tou output uri.  */

    protected fun cropImage() {
        if (mOptions!!.noOutputImage) {
            setResult(null, null, 1)
        } else {
            val outputUri = outputUri
            mCropImageView!!.saveCroppedImageAsync(
                    outputUri,
                    mOptions!!.outputCompressFormat,
                    mOptions!!.outputCompressQuality,
                    mOptions!!.outputRequestWidth,
                    mOptions!!.outputRequestHeight,
                    mOptions!!.outputRequestSizeOptions)
        }
    }

    /** Rotate the image in the crop image view.  */
    protected fun rotateImage(degrees: Int) {
        mCropImageView!!.rotateImage(degrees)
    }

    /** Result with cropped image data or error if failed.  */
    protected fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val resultCode = if (error == null) IMAGE_PREVIEW_REQUEST_CODE else CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
        setResult(resultCode, getResultIntent(uri, error, sampleSize))
        finish()
    }

    /** Cancel of cropping activity.  */
    protected fun setResultCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    /** Get intent instance to be used for the result of this activity.  */
    protected fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {
        val result = mCropImageView!!.imageUri?.let {
            mCropImageView!!.cropRect?.let { it1 ->
                mCropImageView!!.wholeImageRect?.let { it2 ->
                    error?.let { it3 ->
                        CropImage.ActivityResult(
                                it,
                                uri!!,
                                it3,
                                mCropImageView!!.cropPoints,
                                it1,
                                mCropImageView!!.rotatedDegrees,
                                it2,
                                sampleSize)
                    }
                }
            }
        }


        val intent = Intent()
        intent.putExtras(getIntent())
        if (uri != null) {
            intent.putExtra("Path",uri.path)
            intent.putExtra("position",position)
        }
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)
        return intent
    }

    /** Update the color of a specific menu item to the given color.  */
    private fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        val menuItem = menu.findItem(itemId)
        if (menuItem != null) {
            val menuItemIcon = menuItem.icon
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate()
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    menuItem.icon = menuItemIcon
                } catch (e: Exception) {
                    Log.w("AIC", "Failed to update menu item color", e)
                }

            }
        }
    }

}
