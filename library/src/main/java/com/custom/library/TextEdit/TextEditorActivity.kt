package com.custom.library.TextEdit

import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.custom.library.Crop.BitmapUtils
import com.custom.library.R
import com.custom.library.ui.ImageEditActivity
import com.custom.library.util.Utils.calculateInSampleSize
import kotlinx.android.synthetic.main.activity_adjustment.*
import kotlinx.android.synthetic.main.activity_adjustment.adjustment_iv
import kotlinx.android.synthetic.main.activity_text_editor.*
import android.graphics.Bitmap
import android.view.View.DragShadowBuilder
import android.content.ClipData
import android.content.ClipDescription
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TextEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.custom.library.R.layout.activity_text_editor)

        val path = Uri.parse(intent.getStringExtra("Path"))

        adjustment_iv.setImageURI(path)



        text_writer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()
                text_preview.text = text
            }
        })

        save.setOnClickListener {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)
            var displayMetrics = resources.displayMetrics
            options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels)
            options.inJustDecodeBounds = false
            options.inMutable = true
            var bitmap = BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)

           // val mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig())
           // bitmap.recycle()
            var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            var canvas  = Canvas(mutableBitmap)
            var paint =  Paint()
            paint.color = resources.getColor(R.color.blue)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 100F
          //  canvas.drawText(text_preview.text.toString(), (mutableBitmap.width / 2).toFloat(), (mutableBitmap.height / 2).toFloat(), paint)
            canvas.drawText(text_preview.text.toString(), text_preview.x,text_preview.y, paint)


            val uri = BitmapUtils.writeTempStateStoreBitmap(this,mutableBitmap,null)

            val intent = Intent()
            intent.putExtras(getIntent())
            if (uri != null) {
                intent.putExtra("Path",uri.path)
            }

            setResult(ImageEditActivity.TEXT_EDIT_IMAGE_REQUEST_CODE,intent)
            finish()

        }

        var listener = View.OnTouchListener(function = {view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                view.y = motionEvent.rawY - view.height/2
                view.x = motionEvent.rawX - view.width/2
            }

            true

        })

        // Declared in our activity_shapes_view.xml file.
        text_preview.setOnTouchListener(listener)

/*
        text_preview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(view == null) return false

                val item = ClipData.Item(view.getTag() as CharSequence)

                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(view.getTag().toString(), mimeTypes, item)
                val shadowBuilder = View.DragShadowBuilder(view)

                view.startDrag(data, //data to be dragged
                        shadowBuilder, //drag shadow
                        view, //local data about the drag and drop operation
                        0   //no needed flags
                )
                view.visibility = View.INVISIBLE
                return view?.onTouchEvent(event) ?: true
            }
        })
*/


      /*  text_preview.setOnTouchListener { view, event ->

            val item = ClipData.Item(view.getTag() as CharSequence)

            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(view.getTag().toString(), mimeTypes, item)
            val shadowBuilder = View.DragShadowBuilder(view)

            view.startDrag(data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            )
            view.visibility = View.INVISIBLE
            true
          //  view.visibility = View.INVISIBLE

        }*/


    }
}


