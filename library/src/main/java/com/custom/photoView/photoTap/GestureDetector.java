package com.custom.photoView.photoTap;

import android.view.MotionEvent;

import com.custom.photoView.photoTap.OnGestureListener;

public interface GestureDetector {

    boolean onTouchEvent(MotionEvent ev);

    boolean isScaling();

    boolean isDragging();

    void setOnGestureListener(OnGestureListener listener);

}
