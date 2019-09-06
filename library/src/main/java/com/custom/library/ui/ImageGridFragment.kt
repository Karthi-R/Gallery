package com.custom.library.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.library.*
import com.custom.library.Crop.CropImage
import com.custom.library.Crop.CropImage.CROP_IMAGE_EXTRA_OPTIONS
import com.custom.library.Crop.CropImage.CROP_IMAGE_EXTRA_SOURCE
import com.custom.library.Crop.CropImageActivity
import com.custom.library.Crop.CropImageOptions
import com.custom.library.adapter.ImageFolderAdapter
import com.custom.library.adapter.ImageRecyclerAdapter
import com.custom.library.bean.ImageFolder
import com.custom.library.bean.ImageItem
import com.custom.library.util.CameraUtil
import com.custom.library.util.Utils
import com.custom.library.util.isSameDate
import com.custom.library.view.FolderPopUpWindow
import com.custom.library.view.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_image_grid.*
import kotlinx.android.synthetic.main.include_top_bar.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hubert
 *
 * Created on 2017/10/12.
 */
//class ImageGridFragment(mActivity: Activity, imageDataSource: MutableList<ImageFolder>?) : Fragment(), View.OnClickListener, ImageDataSource.OnImagesLoadedListener, ImageRecyclerAdapter.OnImageItemClickListener {
class ImageGridFragment(mActivity: Activity, imgFolderList: MutableList<ImageFolder>?) : Fragment(), View.OnClickListener, ImageRecyclerAdapter.OnImageItemClickListener {
    companion object {
//Uri.fromFile(File.createTempFile
        val REQUEST_PERMISSION_STORAGE = 0x12
        val REQUEST_PERMISSION_CAMERA = 0x13
        val REQUEST_CAMERA = 0x23
        val REQUEST_PREVIEW = 0x9
        val REQUEST_CROP = 0x10
        val INTENT_MAX = 1000


        fun startForResult(activity: Activity, requestCode: Int, takePhoto: Boolean) {
            val intent = Intent(activity, ImageGridFragment::class.java)
            intent.putExtra(C.EXTRA_TAKE_PHOTO, takePhoto)
            activity.startActivityForResult(intent, requestCode)
        }


/*
            fun newInstance(imgFolderList: MutableList<ImageFolder>?, position: Int) = ImageGridFragment().apply {
                this.imgFolderList = imgFolderList
                this.position = position

                val bundle = Bundle()
                bundle.putParcelableArrayList("list", ArrayList<Parcelable>(imgFolderList))
               // bundle.putParcelableArrayList("list",imgFolderList?.toList() as ArrayList<ImageFolder>)
                bundle.putInt("Selected_position", position)
                bundle.putString("test", "test")


                val bundle = Bundle()
                bundle.putInt("Selected_position", position)
                bundle.putParcelableArrayList("FolderList", imgFolderList?.toList() as ArrayList<ImageFolder>)
                setArguments(bundle)
            }
*/
    }

    private val pickerHelper: PickHelper = ImagePicker.pickHelper
   // position=mPosition
    //private val imageDataSource = imageDataSource
    private val imgFolderList = imgFolderList
   // private val imageDataSource = mActivity?.let { ImageDataSource(it as FragmentActivity) }
    private lateinit var adapter: ImageRecyclerAdapter
    private lateinit var mFolderPopupWindow: FolderPopUpWindow
    private lateinit var mImageFolderAdapter: ImageFolderAdapter
    private lateinit var imageFolders: List<ImageFolder>
    private lateinit var takeImageFile: File
    private var takePhoto: Boolean = false
    var position=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position= arguments?.getInt("Selected_position")!!
       /* val test= arguments?.getString("test")
      //  imgFolderList = arguments?.getParcelableArrayList("FolderList")
        imgFolderList = arguments?.getParcelableArrayList("list")*/
        setHasOptionsMenu(true)
        //setContentView(R.layout.activity_image_grid)
      //  takePhoto = intent.extras[C.EXTRA_TAKE_PHOTO] as Boolean
       /* if (takePhoto) {
            onCameraClick()
        }

        initView()
        initPopWindow()
        loadData()*/


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

/*
            if (imageDataSource != null) {
                imageDataSource.loadImage(this)
            }
*/
        }
    }

    override fun onResume() {
        super.onResume()
        //数据刷新
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
                    Log.d("hubert", "图片，position：$position ,addTime: $addTime")
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
        with(preview){
            if (pickerHelper.selectedImages.size == 0) {
                isEnabled = false
                title = getString(R.string.ip_complete)
                (resources.getColor(R.color.ip_text_secondary_inverted))
            } else {
                isEnabled = true
                title = getString(R.string.preview,pickerHelper.selectedImages.size, pickerHelper.limit)
            }
        }

       // menu.findItem(R.id.preview).setTitle( getString(R.string.preview,pickerHelper.selectedImages.size, pickerHelper.limit))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.preview -> {
                // getString(R.string.preview,pickerHelper.selectedImages.size, pickerHelper.limit)
                if(pickerHelper.selectedImages.size>0){
                    ImagePreviewActivity.startForResult(this.requireActivity(), ImageGridFragment.REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
                } else{
                    showToast("Select image to preview")
                }
            }
            android.R.id.home ->
            {
                this.requireActivity().finish()
            }

        }

        return false
    }


    private fun initPopWindow() {
        ll_dir.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_preview.setOnClickListener(this)

        mImageFolderAdapter = ImageFolderAdapter(this.requireActivity(), null)
        mFolderPopupWindow = FolderPopUpWindow(this.requireActivity(), mImageFolderAdapter)
        mFolderPopupWindow.setOnItemClickListener(object : FolderPopUpWindow.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                // initView()
                mImageFolderAdapter.selectIndex = position
                mFolderPopupWindow.dismiss()
                val imageFolder = adapterView.adapter?.getItem(position) as ImageFolder
                adapter.refreshData(imageFolder.images)
                tv_dir.text = imageFolder.name
            }
        })
       // footer_bar.post({ mFolderPopupWindow.setMargin(footer_bar.height) })
    }

    private fun showPopupFolderList() {
        mImageFolderAdapter.refreshData(imageFolders.toMutableList())  //刷新数据
        if (mFolderPopupWindow.isShowing) {
            mFolderPopupWindow.dismiss()
        } else {
            mFolderPopupWindow.showAtLocation(footer_bar, Gravity.NO_GRAVITY, 0, 0)
            //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
            var index = mImageFolderAdapter.selectIndex
            index = if (index == 0) index else index - 1
            mFolderPopupWindow.setSelection(index)
        }
    }

    override fun onImageItemClick(imageItem: ImageItem, position: Int) {
        if (pickerHelper.isMultiMode) {
            var images = adapter.images
            var p = position
            if (images.size > INTENT_MAX) {//数据量过大
                val s: Int
                val e: Int
                if (position < images.size / 2) {//点击position在list靠前
                    s = Math.max(position - INTENT_MAX / 2, 0)
                    e = Math.min(s + INTENT_MAX, images.size)
                } else {
                    e = Math.min(position + INTENT_MAX / 2, images.size)
                    s = Math.max(e - INTENT_MAX, 0)
                }
                p = position - s
                Log.e("hubert", "start:$s , end:$e , position:$p")
//            images = ArrayList()
//            for (i in s until e) {
//                images.add(adapter.images[i])
//            }
                //等同于上面，IDE提示换成的Kotlin的高阶函数
                images = (s until e).mapTo(ArrayList()) { adapter.images[it] }
            }
            ImagePreviewActivity.startForResult(this.requireActivity(), REQUEST_PREVIEW, p, images)
        } else {
            pickerHelper.selectedImages.clear()
            pickerHelper.selectedImages.add(imageItem)
            if (pickerHelper.isCrop) {//需要裁剪

                ImageCropActivity.start(this.requireActivity(), REQUEST_CROP)
            } else {
                setResult()
            }
        }
    }

    override fun onCheckChanged(selected: Int, limit: Int) {
        activity?.invalidateOptionsMenu()

    /*    if (selected == 0) {
            btn_ok.isEnabled = false
            btn_ok.text = getString(R.string.ip_complete)
            btn_ok.setTextColor(resources.getColor(R.color.ip_text_secondary_inverted))
            btn_preview.isEnabled = false
            btn_preview.text = getString(R.string.ip_preview)
            btn_preview.setTextColor(resources.getColor(R.color.ip_text_secondary_inverted))
        } else {
            btn_ok.isEnabled = true
            btn_ok.text = getString(R.string.ip_select_complete, selected, limit)
            btn_ok.setTextColor(resources.getColor(R.color.ip_text_primary_inverted))
            btn_preview.isEnabled = true
            btn_preview.text = getString(R.string.ip_preview_count, selected)
            btn_preview.setTextColor(resources.getColor(R.color.ip_text_primary_inverted))
        }*/

    }

    override fun onCameraClick() {
        if (ActivityCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA),
                    ImageGridActivity.REQUEST_PERMISSION_CAMERA)
        } else {
            takeImageFile = CameraUtil.takePicture(this.requireActivity(), REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adapter.refreshData(arguments?.getInt("Selected_position")?.let { imgFolderList?.get(it)?.images })
/*
                if (imageDataSource != null) {
                    imageDataSource.loadImage(this)
                }
*/
            } else {
                showToast("权限被禁止，无法选择本地图片")
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImageFile = CameraUtil.takePicture(this.requireActivity(), REQUEST_CAMERA)
            } else {
                showToast("权限被禁止，无法打开相机")
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
                Log.e("hubert", takeImageFile.absolutePath)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(takeImageFile)
                this.requireActivity().sendBroadcast(mediaScanIntent)

                val imageItem = ImageItem(takeImageFile.absolutePath)
                pickerHelper.selectedImages.clear()
                pickerHelper.selectedImages.add(imageItem)

                if (pickerHelper.isCrop) {
                    ImageCropActivity.start(this.requireActivity(), REQUEST_CROP)
                } else {
                    setResult()
                }
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

    /*    if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("hubert", takeImageFile.absolutePath)
                //广播通知新增图片
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(takeImageFile)
                this.requireActivity().sendBroadcast(mediaScanIntent)

                val imageItem = ImageItem(takeImageFile.absolutePath)
                pickerHelper.selectedImages.clear()
                pickerHelper.selectedImages.add(imageItem)

                if (pickerHelper.isCrop) {//需要裁剪

                    val mSource = Uri.parse(takeImageFile.absolutePath)
                    val intent = Intent()
                    intent.setClass(this.requireActivity(), CropImageActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable(CROP_IMAGE_EXTRA_SOURCE, mSource)
                    bundle.putParcelable(CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
                    intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
                    startActivity(intent)
                    //finish()


                    // ImageCropActivity.start(this, REQUEST_CROP)
                } else {
                    setResult()
                }
            } else if (takePhoto) {//直接拍照返回时不再展示列表
                this.requireActivity().finish()
            }
        } else if (requestCode == REQUEST_PREVIEW) {//预览界面返回
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        } else if (requestCode == REQUEST_CROP) {//裁剪结果
            if (resultCode == Activity.RESULT_OK) {
                setResult()
            }
        }*/
    }



    private fun setResult() {
        val result = Intent()
        result.putExtra(C.EXTRA_IMAGE_ITEMS, pickerHelper.selectedImages)
        this.requireActivity().setResult(Activity.RESULT_OK, result)
        this.requireActivity().finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_dir -> showPopupFolderList()
            R.id.btn_ok -> setResult()
            R.id.btn_preview -> ImagePreviewActivity.startForResult(this.requireActivity(), REQUEST_PREVIEW, 0, pickerHelper.selectedImages)
            R.id.btn_back -> {
                this.requireActivity().setResult(Activity.RESULT_CANCELED)
                this.requireActivity().finish()
            }
        }
    }

/*
    override fun onImagesLoaded(imageFolders: List<ImageFolder>) {
        this.imageFolders = imageFolders
        //showPopupFolderList()

        if (imageFolders.isNotEmpty())
            adapter?.let {
                arguments?.getInt("Selected_position")
                arguments?.getInt("Selected_position")?.let {
                    adapter.refreshData(imageFolders[arguments?.getInt("Selected_position") as Int].images)
                    tv_dir.text = imageFolders[arguments?.getInt("Selected_position") as Int].name
                    recycler.adapter = adapter
                }
            }

*/
/*
        adapter?.let {
                    adapter.refreshData(imageFolders[0].images)
                    tv_dir.text = imageFolders[0].name
                    recycler.adapter = adapter

            }
*//*

    }
*/

    override fun onDestroy() {
        super.onDestroy()
/*
        if (imageDataSource != null) {
            imageDataSource.destroyLoader()
        }
*/
    }

}
