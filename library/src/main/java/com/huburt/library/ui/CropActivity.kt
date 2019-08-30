package com.huburt.library.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.huburt.library.Crop.CropImage
import com.huburt.library.Crop.CropImageOptions
import com.huburt.library.Crop.CropImageView
import com.huburt.library.R
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.IOException

class CropActivity : AppCompatActivity(),com.huburt.library.Crop.CropImageView.OnSetImageUriCompleteListener, com.huburt.library.Crop.CropImageView.OnCropImageCompleteListener {

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
                    outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()))
                } catch (e: IOException) {
                    throw RuntimeException("Failed to create temp file for output image", e)
                }

            }
            return outputUri
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        mCropImageView =  findViewById(R.id.mCropImageView)
       // mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)

        val bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)

        val path=intent.getStringExtra("Path")

        mCropImageView?.setImageUriAsync(Uri.fromFile(File(path)))

     //   mCropImageView?.setImageUriAsync(Uri.fromFile(File(path)))

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            val title = if (mOptions != null &&
                    mOptions!!.activityTitle != null && mOptions!!.activityTitle.length > 0)
                mOptions!!.activityTitle
            else
                getResources().getString(R.string.crop_image_activity_title)
            actionBar!!.setTitle(title)
            actionBar!!.setDisplayHomeAsUpEnabled(true)
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.crop_image_menu, menu)

        if (!mOptions!!.allowRotation) {
            menu.removeItem(R.id.crop_image_menu_rotate_left)
            menu.removeItem(R.id.crop_image_menu_rotate_right)
        } else if (mOptions!!.allowCounterRotation) {
            menu.findItem(R.id.crop_image_menu_rotate_left).isVisible = true
        }

        /* if (!mOptions.allowFlipping) {
      menu.removeItem(R.id.crop_image_menu_flip);
    }*/

        if (mOptions!!.cropMenuCropButtonTitle != null) {
            menu.findItem(R.id.crop_image_menu_crop).setTitle(mOptions!!.cropMenuCropButtonTitle)
        }

        var cropIcon: Drawable? = null
        try {
            if (mOptions!!.cropMenuCropButtonIcon !== 0) {
                cropIcon = ContextCompat.getDrawable(this, mOptions!!.cropMenuCropButtonIcon)
                menu.findItem(R.id.crop_image_menu_crop).icon = cropIcon
            }
        } catch (e: Exception) {
            Log.w("AIC", "Failed to read menu crop drawable", e)
        }

        if (mOptions!!.activityMenuIconColor !== 0) {
            updateMenuItemIconColor(
                    menu, R.id.crop_image_menu_rotate_left, mOptions!!.activityMenuIconColor)
            updateMenuItemIconColor(
                    menu, R.id.crop_image_menu_rotate_right, mOptions!!.activityMenuIconColor)
            // updateMenuItemIconColor(menu, R.id.crop_image_menu_flip, mOptions.activityMenuIconColor);
            if (cropIcon != null) {
                updateMenuItemIconColor(menu, R.id.crop_image_menu_crop, mOptions!!.activityMenuIconColor)
            }
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.crop_image_menu_crop) {
            cropImage()
            return true
        }
        if (item.itemId == R.id.crop_image_menu_rotate_left) {
            rotateImage(-mOptions!!.rotationDegrees)
            return true
        }
        if (item.itemId == R.id.crop_image_menu_rotate_right) {
            rotateImage(mOptions!!.rotationDegrees)
            return true
        }
        /*if (item.getItemId() == R.id.crop_image_menu_flip_horizontally) {
      mCropImageView.flipImageHorizontally();
      return true;
    }
    if (item.getItemId() == R.id.crop_image_menu_flip_vertically) {
      mCropImageView.flipImageVertically();
      return true;
    }*/
        if (item.itemId == android.R.id.home) {
            setResultCancel()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }
    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
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
        setResult(result.uri, result.error, result.sampleSize)
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
        val resultCode = if (error == null) RESULT_OK else CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
       // val result = CropImage.getActivityResult(data)
        //CropImage.getActivityResult(data)
        //mCropImageView!!.setImageUriAsync(uri)
        val intent = Intent(this,SaveEditedImage::class.java)
        if (uri != null) {
            intent.putExtra("Path",uri.path)
        }
        startActivity(intent)
       // setResult(resultCode, getResultIntent(uri, error, sampleSize))
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
                    CropImage.ActivityResult(
                            it,
                            uri!!,
                            error!!,
                            mCropImageView!!.cropPoints,
                            it1,
                            mCropImageView!!.rotatedDegrees,
                            it2,
                            sampleSize)
                }
            }
        }
        val intent = Intent()
        intent.putExtras(getIntent())
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
