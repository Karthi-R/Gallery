package com.custom.photoView.photoTapListener

interface OnGestureListener {


    abstract fun onDrag(dx: Float, dy: Float)

    abstract fun onFling(startX: Float, startY: Float, velocityX: Float,
                         velocityY: Float)

    abstract fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)

}
