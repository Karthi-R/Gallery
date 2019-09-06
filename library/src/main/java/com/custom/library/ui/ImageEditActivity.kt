package com.custom.library.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.custom.library.Adjustment.AdjustmentActivity
import com.custom.library.Edit.CropImage
import com.custom.library.Edit.CropImageOptions
import com.custom.library.R
import com.custom.library.TextEdit.TextEditorActivity
import kotlinx.android.synthetic.main.activity_image_edit.*

class ImageEditActivity : AppCompatActivity() {

    private var path: String = ""
    private var position: Int = 0

    companion object{
        const val CROP_IMAGE_REQUEST_CODE = 501
        const val ADJUSTMENT_IMAGE_REQUEST_CODE = 502
        const val TEXT_EDIT_IMAGE_REQUEST_CODE = 503
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_edit)

        path = intent.getStringExtra("Path")
        position = intent.getIntExtra("position",0)

        preview_imageView.setImageURI(Uri.parse(path))

        btn_back.setOnClickListener { finish() }

        adjustment.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, CropActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
            intent.putExtra("Path", path)
            startActivityForResult(intent,CROP_IMAGE_REQUEST_CODE)
        }

        tone.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, AdjustmentActivity::class.java)
            val bundle = Bundle()
           /* bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)*/
            intent.putExtra("Path", path)
            startActivityForResult(intent,ADJUSTMENT_IMAGE_REQUEST_CODE)
        }

        text_edit.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TextEditorActivity::class.java)
            val bundle = Bundle()
           /* bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)*/
            intent.putExtra("Path", path)
            startActivityForResult(intent,TEXT_EDIT_IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CROP_IMAGE_REQUEST_CODE) {

            if (data != null){
                path = data.getStringExtra("Path")
                preview_imageView.setImageURI(Uri.parse(path))
            }
        } else if(requestCode == ADJUSTMENT_IMAGE_REQUEST_CODE){
            if (data != null){
                path = data.getStringExtra("Path")
                preview_imageView.setImageURI(Uri.parse(path))
            }
        }else if(requestCode == TEXT_EDIT_IMAGE_REQUEST_CODE){
            if (data != null){
                path = data.getStringExtra("Path")
                preview_imageView.setImageURI(Uri.parse(path))
            }
        }
    }

    override fun onBackPressed() {
      //  super.onBackPressed()

        val intent = Intent()
        intent.putExtras(getIntent())
        if (path != null) {
            intent.putExtra("Path",path)
            intent.putExtra("position",position)
        }

        setResult(ImagePreviewActivity.IMAGE_PREVIEW_REQUEST_CODE,intent)
        finish()
    }
}
