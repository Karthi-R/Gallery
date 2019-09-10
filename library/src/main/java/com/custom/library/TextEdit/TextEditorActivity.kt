package com.custom.library.TextEdit

import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.custom.library.CropView.BitmapUtils
import com.custom.library.util.Utils.calculateInSampleSize

import kotlinx.android.synthetic.main.activity_text_editor.*
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import com.custom.library.ui.ImagePreviewActivity
import kotlinx.android.synthetic.main.activity_text_editor.btn_back
import kotlinx.android.synthetic.main.activity_text_editor.cancel
import kotlinx.android.synthetic.main.activity_text_editor.save


class TextEditorActivity : AppCompatActivity(){


    private var _xDelta: Int = 0
    private var _yDelta: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.custom.library.R.layout.activity_text_editor)

        val path = Uri.parse(intent.getStringExtra("Path"))

        edit_iv.setImageURI(path)


        image_lay.tag = "img"
        text_preview.tag = "txt"

      //  image_lay.setOnDragListener(this)
       // text_preview.setOnTouchListener(this)

        cancel.setOnClickListener { finish() }

        btn_back.setOnClickListener { finish() }


        text_writer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()
                text_preview.text = text
            }
        })

        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)
        var displayMetrics = resources.displayMetrics
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels)
        options.inJustDecodeBounds = false
        options.inMutable = true
        var bitmap = BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)

        save.setOnClickListener {


           // val mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig())
           // bitmap.recycle()
            var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            var canvas  = Canvas(mutableBitmap)
            var paint =  Paint()
            paint.color = resources.getColor(com.custom.library.R.color.blue)
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

            setResult(ImagePreviewActivity.TEXT_EDIT_IMAGE_REQUEST_CODE,intent)
            finish()

        }

      /*  var listener = View.OnTouchListener(function = {view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                view.y = motionEvent.rawY - view.height/2
                view.x = motionEvent.rawX - view.width/2
            }

            true

        })*/

        var listener = View.OnTouchListener(function = {view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                //working

            /*    val location = IntArray(2)
                edit_iv.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]

                val eventX = motionEvent.rawX
                val eventY = motionEvent.rawY

                val eventX1 = motionEvent.x
                val eventY1 = motionEvent.y

                val v_x = edit_iv.getTop()
                val v_y =edit_iv.getLeft()


              //  if(((eventX > edit_iv.left) && (eventX < edit_iv.right)) && ((eventY > edit_iv.top) && (eventY < edit_iv.bottom)))     {
                if(((eventX > edit_iv.left) && (eventX < edit_iv.right)) && ((eventY > edit_iv.top) && (eventY < edit_iv.bottom)))     {
                    view.y = motionEvent.rawY - view.height/2
                    view.x = motionEvent.rawX - view.width/2
                }*/
   val location = IntArray(2)
                edit_iv.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]

                val txt_location = IntArray(2)
                text_preview.getLocationOnScreen(txt_location)
                val x1 = txt_location[0]
                val y1 = txt_location[1]

                val eventX = motionEvent.rawX
                val eventY = motionEvent.rawY

                val eventX1 = motionEvent.x
                val eventY1 = motionEvent.y

                val v_x = edit_iv.getTop()
                val v_y =edit_iv.getLeft()


              //  if(((eventX > edit_iv.left) && (eventX < edit_iv.right)) && ((eventY > edit_iv.top) && (eventY < edit_iv.bottom)))     {
                if((x1 < bitmap.width)&& (y1 < bitmap.height))     {
                    view.y = motionEvent.rawY - view.height/2
                    view.x = motionEvent.rawX - view.width/2
                }


            }

            true

        })

        text_preview.setOnTouchListener(listener)



        // Declared in our activity_shapes_view.xml file.







    }

  /*  override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val item = ClipData.Item(view?.tag as String)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(view.getTag() as String, mimeTypes, item)
        val shadowBuilder = View.DragShadowBuilder(view)
        view.startDrag(data, shadowBuilder,view, 0)
        view.setVisibility(View.INVISIBLE)

                return true

    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        var action = event!!.getAction()
        when (action) {
             DragEvent.ACTION_DRAG_STARTED ->{

             }

             DragEvent.ACTION_DRAG_ENTERED->{

            }
             DragEvent.ACTION_DRAG_EXITED->{

             }

             DragEvent.ACTION_DROP ->{

                 val textDropped = event.getLocalState() as TextView
                 textDropped.visibility = View.VISIBLE
                 val viewDroppedAt = v as ViewGroup
                 (textDropped.parent as ViewGroup).removeView(textDropped)

                 if (v.getId() === com.custom.library.R.id.image_lay) {
                     if (textDropped.id == com.custom.library.R.id.text_preview) {
                         textDropped.text = "Item1 in bottom layer"
                         textDropped.setTextColor(resources.getColor(com.custom.library.R.color.blue))
                         textDropped.textSize= 50F
                         textDropped.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

                     }
                 }

                 viewDroppedAt.addView(textDropped)

                // v.setVisibility(View.VISIBLE)
             }


             DragEvent.ACTION_DRAG_ENDED->{

             }



        }
        return true
    }*/

}


