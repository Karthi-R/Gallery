package com.custom.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.custom.library.R

/**
 * Created by Kumuthini N on 2019-08-29.
 */


class GalleryViewFragment : Fragment() {

    private var textView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_view, container, false)

        textView = view.findViewById(R.id.id_txt_display)
        val msg = getArguments()?.getString("msg")
        textView!!.setText(msg)

        return view
    }

}