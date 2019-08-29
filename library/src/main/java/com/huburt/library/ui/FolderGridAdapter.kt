package com.huburt.library.ui
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.huburt.library.ImagePicker
import com.huburt.library.R
import com.huburt.library.bean.ImageFolder
import com.huburt.library.util.Utils
import java.util.ArrayList

class FolderGridAdapter (private val mActivity: Activity, folders: MutableList<ImageFolder>?) : BaseAdapter() {
    private val mInflater: LayoutInflater
    private val mImageSize: Int
    private var imageFolders: MutableList<ImageFolder>? = null
    var selectIndex = 0
        set(i) {
            if (selectIndex != i) {
                field = i
                notifyDataSetChanged()
            }
        }

    init {
        imageFolders = if (folders != null && folders.size > 0) folders else ArrayList()
        mImageSize = Utils.getImageItemWidth(mActivity)
        mInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun refreshData(folders: MutableList<ImageFolder>?) {
        if (folders != null && folders.size > 0) imageFolders = folders
        else imageFolders?.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = imageFolders?.size ?: 0

    override fun getItem(position: Int): ImageFolder = imageFolders!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = mInflater.inflate(R.layout.activity_folder_grid_adapter, parent, false)
            holder = ViewHolder(view)
        } else {
            holder = view.tag as ViewHolder
        }

        val (name, _, cover, images) = getItem(position)
        holder.folderName.text = name
        holder.imageCount.text = mActivity.getString(R.string.ip_folder_image_count, images.size)
        if (cover?.path != null) {
            ImagePicker.imageLoader?.displayImage(mActivity, cover.path!!, holder.cover, mImageSize, mImageSize)
        }

       /* if (selectIndex == position) {
            holder.folderCheck.visibility = View.VISIBLE
        } else {
            holder.folderCheck.visibility = View.INVISIBLE
        }*/

        return view!!
    }

    private inner class ViewHolder(view: View) {
        internal var cover: ImageView = view.findViewById(R.id.imageView) as ImageView
        internal var folderName: TextView = view.findViewById(R.id.textView) as TextView
        internal var imageCount: TextView = view.findViewById(R.id.tv_image_count) as TextView
       // internal var folderCheck: ImageView = view.findViewById(R.id.iv_folder_check) as ImageView

        init {
            view.tag = this
        }
    }
}

/*
class FolderGridAdapter (context: Context, arrayListImage: ArrayList<Int>, name: Array<String>) : BaseAdapter() {

    //Passing Values to Local Variables

    var context = context
    var arrayListImage = arrayListImage
    var name = name


    //Auto Generated Method
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myView = convertView
        var holder: ViewHolder


        if (myView == null) {

            //If our View is Null than we Inflater view using Layout Inflater

            val mInflater = (context as Activity).layoutInflater

            //Inflating our grid_item.
            myView = mInflater.inflate(R.layout.activity_folder_grid_adapter, parent, false)

            //Create Object of ViewHolder Class and set our View to it

            holder = ViewHolder()


            //Find view By Id For all our Widget taken in grid_item.

            */
/*Here !! are use for non-null asserted two prevent From null.
             you can also use Only Safe (?.)

            *//*



            holder.mImageView = myView!!.findViewById(R.id.imageView) as ImageView
            holder.mTextView = myView!!.findViewById(R.id.textView) as TextView

            //Set A Tag to Identify our view.
            myView.setTag(holder)


        } else {

            //If Our View in not Null than Just get View using Tag and pass to holder Object.

            holder = myView.getTag() as ViewHolder

        }

        //Setting Image to ImageView by position
        holder.mImageView!!.setImageResource(arrayListImage.get(position))

        //Setting name to TextView by position
        holder.mTextView!!.setText(name.get(position))

        return myView

    }

    //Auto Generated Method
    override fun getItem(p0: Int): Any {
        return arrayListImage.get(p0)
    }

    //Auto Generated Method
    override fun getItemId(p0: Int): Long {
        return 0
    }

    //Auto Generated Method
    override fun getCount(): Int {
        return arrayListImage.size
    }


    //Create A class To hold over View like we taken in grid_item.xml

    class ViewHolder {

        var mImageView: ImageView? = null
        var mTextView: TextView? = null

    }

}*/
