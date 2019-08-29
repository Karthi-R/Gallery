package com.huburt.library.ui

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by hubert
 *
 * Created on 2017/10/12.
 */
open class BaseActivity : AppCompatActivity() {
    fun showToast(charSequence: CharSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show()
    }
}