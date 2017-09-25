package com.example.aa.puzzle;

import android.graphics.Bitmap;

/**
 * Created by aa on 2017/9/23.
 */

public class ItemBean {
    private int mBitmapId;
    private int mItemId;
    private Bitmap mBitmap;
    public ItemBean(){

    }
    public ItemBean(int mItemId,int mBitmapId,Bitmap mBitmap){
        this.mItemId=mItemId;
        this.mBitmap=mBitmap;
        this.mBitmapId=mBitmapId;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public int getmBitmapId() {
        return mBitmapId;
    }

    public int getmItemId() {
        return mItemId;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setmBitmapId(int mBitmapId) {
        this.mBitmapId = mBitmapId;
    }

    public void setmItemId(int mItemId) {
        this.mItemId = mItemId;
    }
}
