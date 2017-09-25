package com.example.aa.puzzle;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private PopupWindow mPopupWindow;
    private TextView SelectText;
    private int type=3;
    private GridAdapter adapter;
    private GridView gridView;
    private List<Bitmap> mPics;
    private String[] dialogItem={"本地图册","拍照"};
    private static final int RESULT_IMAGE=100;
    private static final int RESULT_CAMERA=200;
    private Uri photoUri;
    private Toolbar toolbar;
    public static String TEMP_IMAGE_PATH=Environment.getExternalStorageDirectory().getPath()+"/puzzle/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
        initColor();
        initView();
        initImage();}
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initImage() {
        final int[] picId;
        picId=new int[]{
                R.drawable.pn0,R.drawable.pn1,R.drawable.pn2, R.drawable.pn3,
                R.drawable.pn4,R.drawable.pn5,R.drawable.pn6,R.drawable.pn7,
                R.drawable.pn8,R.drawable.pn9,R.drawable.pn10,R.drawable.pn11,
                R.drawable.pn12,R.drawable.add
        };
        mPics=new ArrayList<>();
        for (int i=0;i<picId.length;i++){
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(),picId[i]);
            mPics.add(bitmap);
        }
        adapter=new GridAdapter(this,mPics);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==mPics.size()-1){
                    showDialog();
                }
                else {
                    Intent intent=new Intent(MainActivity.this,Puzzle.class);
                    intent.putExtra("mPicId",picId[position]);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择");
        builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==1){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                     openCamera();
                    else requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RESULT_CAMERA);
                }
                else if (which==0){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                    else requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RESULT_IMAGE);
                }
            }
        });
        builder.create().show();
    }

    private void openAlbum() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,RESULT_IMAGE);
    }

    private void openCamera() {
        try {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            new File(TEMP_IMAGE_PATH).mkdir();//新建文件夹
            TEMP_IMAGE_PATH=TEMP_IMAGE_PATH+String.valueOf(Math.random()*2000)+"image.png";
            File file=new File(TEMP_IMAGE_PATH);
            file.createNewFile();
            if (Build.VERSION.SDK_INT>=24)
            photoUri=FileProvider.getUriForFile(this,"com.example.aa.puzzle.fileProvider",file);
            else photoUri=Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            startActivityForResult(intent,RESULT_CAMERA);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RESULT_CAMERA:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    openCamera();
                break;
            case RESULT_IMAGE:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==RESULT_CAMERA){
                    //Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                    //MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,String.valueOf(Math.random())+"image","image");
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + TEMP_IMAGE_PATH)));
                    Intent intent=new Intent(MainActivity.this,Puzzle.class);
                    intent.putExtra("mPicPath",TEMP_IMAGE_PATH);
                    intent.putExtra("type",type);
                    startActivity(intent);
            }
            else if (requestCode==RESULT_IMAGE){
                    Uri uri=data.getData();
                    String imagePath=null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    imagePath=handleUriToPath(uri);
                }
                else imagePath=getImagePath(uri,null);
                    Intent intent=new Intent(MainActivity.this,Puzzle.class);
                    intent.putExtra("mPicPath",imagePath);
                    intent.putExtra("type",type);
                    startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String handleUriToPath(Uri uri) {
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                return getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                return getImagePath(contentUri,null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme()))
            return getImagePath(uri,null);
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();
        return null;
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        cursor.close();
        return path;
    }


    private void initView() {
        SelectText=(TextView)findViewById(R.id.puzzle_main_type_selected);
        gridView=(GridView)findViewById(R.id.gv_xpuzzle_main_pic_list);
        SelectText.setOnClickListener(this);
    }

    private void initColor() {
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant=palette.getVibrantSwatch();
                Palette.Swatch vibrant2=palette.getLightVibrantSwatch();
                GradientDrawable drawable=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{vibrant.getRgb(),vibrant2.getRgb()});
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    toolbar.setBackground(drawable);
                }
                else toolbar.setBackgroundColor(vibrant.getRgb());
                Window window=getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(vibrant.getRgb());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.puzzle_main_type_selected:
                type=type+1;
                if (type>4) type=2;
                break;
        }
        SelectText.setText(type+"×"+type);
    }
    
}
