package com.huburt.library.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huburt.library.R
import kotlinx.android.synthetic.main.activity_process_edited_image.*

class SaveEditedImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_edited_image)

        edited_image.setImageURI(Uri.parse(intent.getStringExtra("Path")))

        cancel.setOnClickListener { finish() }
        btn_back.setOnClickListener { finish() }
    }
}
