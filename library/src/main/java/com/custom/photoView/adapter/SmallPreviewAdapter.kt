package com.custom.photoView.adapter

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.custom.photoView.ImagePicker
import com.custom.photoView.R
import com.custom.photoView.bean.ImageItem
import java.util.*


class SmallPreviewAdapter(
        private val mActivity: Activity,
        var images: List<ImageItem> = ArrayList()
) : RecyclerView.Adapter<SmallPreviewAdapter.SmallPreviewViewHolder>() {


    var current: ImageItem? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallPreviewViewHolder {

        return SmallPreviewViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_small_preview, parent, false))
    }

    override fun onBindViewHolder(holder: SmallPreviewViewHolder, position: Int) {
        holder?.bind(position)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    inner class SmallPreviewViewHolder(private var mView: View) : RecyclerView.ViewHolder(mView) {

        val iv_small = mView.findViewById(R.id.iv_small) as ImageView
        val v_frame = mView.findViewById(R.id.v_frame) as View

        fun bind(position: Int) {
            mView.setOnClickListener {
                listener?.onItemClick(position, images[position])
            }
            if (TextUtils.equals(current?.path, images[position].path)) {
                v_frame.visibility = View.VISIBLE
            } else {
                v_frame.visibility = View.GONE
            }
            images[position].path?.let { ImagePicker.imageLoader.displayImage(mActivity, it, iv_small, iv_small.width, iv_small.height) }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, imageItem: ImageItem)
    }
}