package com.custom.library.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.custom.library.ImagePicker.pickHelper
import com.custom.library.R
import com.custom.library.util.FileUtil
import kotlinx.android.synthetic.main.activity_process_edited_image.*

class SaveEditedImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_edited_image)
        ///data/user/0/com.custom.gallery/cache/cropped-2122258715.jpg

        edited_image.setImageURI(Uri.parse(intent.getStringExtra("Path")))

        cancel.setOnClickListener { finish() }
        btn_back.setOnClickListener { finish() }

/*
        save.setOnClickListener {
            edited_image.saveBitmapToFile(FileUtil.getCropCacheFolder(this), pickHelper.outPutX, pickHelper.outPutY, pickHelper.isSaveRectangle)
        }
*/
    }
}
