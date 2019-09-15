package com.custom.photoView.photoTapListener

import android.content.Context
import android.os.Build

object VersionedGestureDetector {

    fun newInstance(context: Context,
                    listener: OnGestureListener): GestureDetector {
        val sdkVersion = Build.VERSION.SDK_INT
        val detector: GestureDetector

        if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
            detector = CupcakeGestureDetector(context)
        } else if (sdkVersion < Build.VERSION_CODES.FROYO) {
            detector = EclairGestureDetector(context)
        } else {
            detector = FroyoGestureDetector(context)
        }

        detector.setOnGestureListener(listener)

        return detector
    }

}
