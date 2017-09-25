package com.example.aa.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by aa on 2017/9/19.
 */

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> pics;
    public GridAdapter(Context context, List<Bitmap> pics){
        this.context=context;
        this.pics=pics;
    }
    @Override
    public int getCount() {
        return pics.size();
    }

    @Override
    public Object getItem(int position) {
        return pics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView==null){
            image=new ImageView(context);
            image.setLayoutParams(new GridView.LayoutParams(240,300));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else image=(ImageView) convertView;
        image.setBackgroundColor(Color.BLACK);
        image.setImageBitmap(pics.get(position));
        return image;
    }
}
