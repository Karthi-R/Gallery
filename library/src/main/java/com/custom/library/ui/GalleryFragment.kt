package com.custom.library.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.library.C
import com.custom.library.ImagePicker
import com.custom.library.PickHelper
import com.custom.library.R
import com.custom.library.adapter.ImageRecyclerAdapter
import com.custom.library.bean.ImageFolder
import com.custom.library.bean.ImageItem
import com.custom.library.util.CameraUtil
import com.custom.library.util.Utils
import com.custom.library.util.isSameDate
import com.custom.library.view.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_image_grid.*
import kotlinx.android.synthetic.main.include_top_bar.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*





class GalleryFragment(mActivity: Activity, imgFolderList: MutableList<ImageFolder>?) : Fragment(), View.OnClickListener, ImageRecyclerAdapter.OnImageItemClickListener {
    companion object {
        val REQUEST_PERMISSION_STORAGE = 0x12
        val REQUEST_PERMISSION_CAMERA = 0x13
        val REQUEST_CAMERA = 0x23
        val REQUEST_PREVIEW = 0x9
        val REQUEST_CROP = 0x10
        val INTENT_MAX = 1000


        fun startForResult(activity: Activity, requestCode: Int, takePhoto: Boolean) {
            val intent = Intent(activity, GalleryFragment::class.java)
            intent.putExtra(C.EXTRA_TAKE_PHOTO, takePhoto)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private val pickerHelper: PickHelper = ImagePicker.pickHelper
    private val imgFolderList = imgFolderList
    private lateinit var adapter: ImageRecyclerAdapter
    private lateinit var takeImageFile: File
    private var takePhoto: Boolean = false
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("Selected_position")!!

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_image_grid, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        if (takePhoto) {
            onCameraClick()
        }

        initView()
        initPopWindow()
        loadData()

        ll_dir.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_preview.setOnClickListener(this)
    }

    private fun loadData() {
        if (ActivityCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        } else {
            adapter.refreshData(arguments?.getInt("Selected_position")?.let { imgFolderList?.get(it)?.images })
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        onCheckChanged(pickerHelper.selectedImages.size, pickerHelper.limit)
    }

    private fun initView() {

        recycler.layoutManager = GridLayoutManager(this.requireActivity(), 3)
        recycler.addItemDecoration(GridSpacingItemDecoration(3, Utils.dp2px(this.requireActivity(), 2f), false))
        adapter = ImageRecyclerAdapter(this.requireActivity(), pickerHelper)
        ImagePicker.showCamera(false)
        recycler.adapter = adapter

        adapter.listener = this
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (tv_date.visibility == View.VISIBLE) {
                        tv_date.animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                        tv_date.visibility = View.GONE

                    }
                } else {
                    if (tv_date.visibility == View.GONE) {
                        tv_date.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                        tv_date.visibility = View.VISIBLE
                    }
                    val gridLayoutManager = recycler.layoutManager as GridLayoutManager
                    val position = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val addTime = adapter.getItem(position)?.addTime
                    if (addTime != null) {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = addTime * 1000
                        if (isSameDate(calendar.time, Calendar.getInstance().time)) {
                            tv_date.text = "Today"
                        } else {
                            val format = SimpleDateFormat("yyyy/MM", Locale.getDefault())
                            tv_date.text = format.format(calendar.time)
                        }
                    }
                }
            }
        })

        if (pickerHelper.isMultiMode) {
            btn_ok.visibility = View.VISIBLE
            btn_preview.visibility = View.VISIBLE
        } else {
            btn_ok.visibility = View.GONE
            btn_preview.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.preview_menu, menu)
        val preview =  menu.findItem(R.id.preview)
        menuIconColor(preview,R.color.colorAccent)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.preview -> {
                if(pickerHelper.selectedImages.size>0){
                    ImagePreviewActivity.startForResult(this.requireActivity(), GalleryFragment.REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
                } else{
                    showToast("Select image to preview")
                }
            }
            android.R.id.home ->
            {
                this.requireActivity().finish()
            }

        }

        return true
    }

    fun menuIconColor(menuItem: MenuItem, color: Int) {
        val drawable = menuItem.icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }


    private fun initPopWindow() {
        ll_dir.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_preview.setOnClickListener(this)
    }

    override fun onImageItemClick(imageItem: ImageItem, position: Int) {
        if (pickerHelper.isMultiMode) {
            var images = adapter.images
            var p = position
            if (images.size > INTENT_MAX) {
                val s: Int
                val e: Int
                if (position < images.size / 2) {
                    s = Math.max(position - INTENT_MAX / 2, 0)
                    e = Math.min(s + INTENT_MAX, images.size)
                } else {
                    e = Math.min(position + INTENT_MAX / 2, images.size)
                    s = Math.max(e - INTENT_MAX, 0)
                }
                p = position - s
                images = (s until e).mapTo(ArrayList()) { adapter.images[it] }
            }
            ImagePreviewActivity.startForResult(this.requireActivity(), REQUEST_PREVIEW, p, images)
        } else {
            pickerHelper.selectedImages.clear()
            pickerHelper.selectedImages.add(imageItem)
        }
    }

    override fun onCheckChanged(selected: Int, limit: Int) {
        if (selected <= 0) {
            (activity as GalleryDetailActivity).setActionBarTitle("Select")
        } else {
            (activity as GalleryDetailActivity).setActionBarTitle(getString(R.string.preview, pickerHelper.selectedImages.size, pickerHelper.limit))
        }
    }

    override fun onCameraClick() {
        if (ActivityCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA),
                    ImageCameraActivity.REQUEST_PERMISSION_CAMERA)
        } else {
            takeImageFile = CameraUtil.takePicture(this.requireActivity(), REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adapter.refreshData(arguments?.getInt("Selected_position")?.let { imgFolderList?.get(it)?.images })
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImageFile = CameraUtil.takePicture(this.requireActivity(), REQUEST_CAMERA)
            }
        }
    }

    fun showToast(charSequence: CharSequence) {
        Toast.makeText(this.requireActivity(), charSequence, Toast.LENGTH_SHORT).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                takeImageFile = CameraUtil.takePicture(this.requireActivity(), REQUEST_CAMERA)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(takeImageFile)
                this.requireActivity().sendBroadcast(mediaScanIntent)

                val imageItem = ImageItem(takeImageFile.absolutePath)
                pickerHelper.selectedImages.clear()
                pickerHelper.selectedImages.add(imageItem)

            } else if (takePhoto) {
                this.requireActivity().finish()
            }
        } else if (requestCode == REQUEST_PREVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        } else if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        }
    }


    private fun setResult() {
        val result = Intent()
        result.putExtra(C.EXTRA_IMAGE_ITEMS, pickerHelper.selectedImages)
        this.requireActivity().setResult(Activity.RESULT_OK, result)
        this.requireActivity().finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ok -> setResult()
            R.id.btn_preview -> ImagePreviewActivity.startForResult(this.requireActivity(), REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
            R.id.btn_back -> {
                this.requireActivity().setResult(Activity.RESULT_CANCELED)
                this.requireActivity().finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
