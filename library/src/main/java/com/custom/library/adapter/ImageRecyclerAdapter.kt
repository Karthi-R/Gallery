package com.custom.library.adapter

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.custom.library.ImagePicker
import com.custom.library.PickHelper
import com.custom.library.R
import com.custom.library.bean.ImageItem
import com.custom.library.util.Utils
import java.util.*


class ImageRecyclerAdapter(
        private val mActivity: Activity,
        private val pickHelper: PickHelper,
        var images: ArrayList<ImageItem> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mImageSize: Int = Utils.getImageItemWidth(mActivity)
    private val mInflater: LayoutInflater = LayoutInflater.from(mActivity)
    internal var listener: OnImageItemClickListener? = null

    interface OnImageItemClickListener {
        fun onImageItemClick(imageItem: ImageItem, position: Int)
        fun onCheckChanged(selected: Int, limit: Int)
        fun onCameraClick()
    }

    fun refreshData(images: ArrayList<ImageItem>?) {
        if (images != null) this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_CAMERA) {
            CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false))
        } else ImageViewHolder(mInflater.inflate(R.layout.adapter_image_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? CameraViewHolder)?.bindCamera() ?: (holder as? ImageViewHolder)?.bind(position)
    }

    override fun getItemViewType(position: Int): Int =
            if (pickHelper.isShowCamera) if (position == 0) ITEM_TYPE_CAMERA else ITEM_TYPE_NORMAL
            else ITEM_TYPE_NORMAL

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = if (pickHelper.isShowCamera) images.size + 1 else images.size

    fun getItem(position: Int): ImageItem? =
            if (pickHelper.isShowCamera)
                if (position == 0) null else images[position - 1]
            else images[position]


    private inner class ImageViewHolder internal constructor(internal var rootView: View) : RecyclerView.ViewHolder(rootView) {

        internal var ivThumb: ImageView = rootView.findViewById(R.id.iv_thumb) as ImageView
        internal var mask: View = rootView.findViewById(R.id.mask)
        internal var checkView: View = rootView.findViewById(R.id.checkView)
        internal var cbCheck: AppCompatCheckBox = rootView.findViewById(R.id.cb_check) as AppCompatCheckBox

        init {
            rootView.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)
        }

        internal fun bind(position: Int) {
            val imageItem = getItem(position)
            ivThumb.setOnClickListener {
                setSelection(imageItem)
            }
            checkView.setOnClickListener {
                setSelection(imageItem)
               /* if (cbCheck.isChecked) {
                    pickHelper.selectedImages.remove(imageItem)
                    mask.visibility = View.GONE
                    cbCheck.isChecked = false
                } else {
                    if (pickHelper.selectedImages.size >= pickHelper.limit) {
                        Toast.makeText(mActivity.applicationContext, mActivity.getString(R.string.ip_select_limit, pickHelper.limit), Toast.LENGTH_SHORT).show()
                    } else {
                        mask.visibility = View.VISIBLE
                        pickHelper.selectedImages.add(imageItem!!)
                        cbCheck.isChecked = true
                    }
                }
                listener?.onCheckChanged(pickHelper.selectedImages.size, pickHelper.limit)*/
            }

            if (pickHelper.isMultiMode) {
                cbCheck.visibility = View.VISIBLE
                if (contains(pickHelper.selectedImages, imageItem)) {
                    mask.visibility = View.VISIBLE
                    cbCheck.isChecked = true
                } else {
                    mask.visibility = View.GONE
                    cbCheck.isChecked = false
                }
            } else {
                cbCheck.visibility = View.GONE
            }
            if (imageItem?.path != null) {
                ImagePicker.imageLoader.displayImage(mActivity, imageItem.path!!, ivThumb, mImageSize, mImageSize)
            }
        }

        private fun setSelection(imageItem: ImageItem?) {
            if (cbCheck.isChecked) {
                pickHelper.selectedImages.remove(imageItem)
                mask.visibility = View.GONE
                cbCheck.isChecked = false
            } else {
                if (pickHelper.selectedImages.size >= pickHelper.limit) {
                    Toast.makeText(mActivity.applicationContext, mActivity.getString(R.string.ip_select_limit, pickHelper.limit), Toast.LENGTH_SHORT).show()
                } else {
                    mask.visibility = View.VISIBLE
                    pickHelper.selectedImages.add(imageItem!!)
                    cbCheck.isChecked = true
                }
            }
            listener?.onCheckChanged(pickHelper.selectedImages.size, pickHelper.limit)
        }

        private fun contains(selectedImages: ArrayList<ImageItem>, imageItem: ImageItem?): Boolean {
            return selectedImages.any { TextUtils.equals(it.path, imageItem?.path) }
        }
    }

    private inner class CameraViewHolder internal constructor(internal var mItemView: View) : RecyclerView.ViewHolder(mItemView) {

        internal fun bindCamera() {
            mItemView.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)
            mItemView.tag = null
            mItemView.setOnClickListener {
                listener?.onCameraClick()
            }
        }
    }

    companion object {

        private val ITEM_TYPE_CAMERA = 0
        private val ITEM_TYPE_NORMAL = 1
    }
}
