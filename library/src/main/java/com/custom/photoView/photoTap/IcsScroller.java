package com.custom.photoView.photoTap;

import android.annotation.TargetApi;
import android.content.Context;

import com.custom.photoView.photoTap.GingerScroller;

@TargetApi(14)
public class IcsScroller extends GingerScroller {

    public IcsScroller(Context context) {
        super(context);
    }

    @Override
    public boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }

}
