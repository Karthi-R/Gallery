package com.custom.photoView.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;


public class BitmapUtil {

    private BitmapUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     *
     *
     * @param path
     * @return
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     *
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     *
     * @param activity
     * @param path
     * @return
     */
    public static Uri getRotatedUri(Activity activity, String path){
        int degree = BitmapUtil.getBitmapDegree(path);
        if (degree != 0){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap newBitmap = BitmapUtil.rotateBitmapByDegree(bitmap,degree);
            return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(),newBitmap,null,null));
        }else{
            return Uri.fromFile(new File(path));
        }
    }

    /**
     *
     *
     * @param path
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotateBitmapByDegree(bitmap,degree);
    }



}
