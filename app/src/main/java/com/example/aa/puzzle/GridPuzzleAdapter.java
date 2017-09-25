package com.example.aa.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by aa on 2017/9/24.
 */

public class GridPuzzleAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap>mPicSelectedList;
    public GridPuzzleAdapter(Context context, List<Bitmap>mPicSelectedList){
        this.context=context;
        this.mPicSelectedList=mPicSelectedList;
    }
    @Override
    public int getCount() {
        return mPicSelectedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicSelectedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView==null){
            imageView=new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(mPicSelectedList.get(position).getWidth(),mPicSelectedList.get(position).getHeight()));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else imageView=(ImageView) convertView;
        imageView.setImageBitmap(mPicSelectedList.get(position));
        return imageView;
    }
}
