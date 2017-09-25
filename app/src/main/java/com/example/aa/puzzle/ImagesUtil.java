package com.example.aa.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aa on 2017/9/23.
 */

public class ImagesUtil {
    private ItemBean itemBean;
    public void createInitBitmaps(int type, Bitmap picSelected, Context context){
        Bitmap bitmap=null;
        List<Bitmap> bitmaps=new ArrayList<>();
        for (int i=1;i<=type;i++)
        {
            for (int j=1;j<=type;j++){
                bitmap=Bitmap.createBitmap(picSelected,(j-1)*picSelected.getWidth()/type,(i-1)*picSelected.getHeight()/type,picSelected.getWidth()/type,picSelected.getHeight()/type);
                itemBean=new ItemBean((i-1)*type+j,(i-1)*type+j,bitmap);
                bitmaps.add(bitmap);
                GameUtil.mItemBeans.add(itemBean);
            }
        }

        Puzzle.mLastBitmap=bitmaps.get(type*type-1);
        bitmaps.remove(type*type-1);
        GameUtil.mItemBeans.remove(type*type-1);
        Bitmap blankBitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.bg);
        blankBitmap=Bitmap.createBitmap(blankBitmap,0,0,picSelected.getWidth()/type,picSelected.getHeight()/type);
        bitmaps.add(blankBitmap);
        GameUtil.mItemBeans.add(new ItemBean(type*type,0,blankBitmap));
        GameUtil.mBlankItemBean=GameUtil.mItemBeans.get(type*type-1);
    }
    public static Bitmap resizeBitmap(float newWidth,float newHeight,Bitmap bitmap){
        Matrix matrix=new Matrix();
        matrix.postScale(newWidth/bitmap.getWidth(),newHeight/bitmap.getHeight());
        Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return newBitmap;
    }
}
