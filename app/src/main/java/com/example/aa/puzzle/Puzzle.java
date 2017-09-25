package com.example.aa.puzzle;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Puzzle extends AppCompatActivity implements View.OnClickListener {
    public static int Type=3;
    public static Bitmap mLastBitmap;
    private Bitmap mPicSelected;
    private Button oldImage;
    private Button resetting;
    private Button back;
    private boolean flag;//是否显示原图
    private GridView gridView;
    private String path=null;
    private int imageId;
    private List<Bitmap>mPicSelectedList=new ArrayList<>();
    private int COUNT_INDEX=0;
    private int COUNT_TIME=0;
    private GridPuzzleAdapter adapter;
    private TextView index;
    private ImageView imageView;
    private TextView timeText;
    private Timer timer;
    private TimerTask timerTask;
    private Toolbar toolbar;
    private Handler mHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:timeText.setText(String.valueOf(COUNT_TIME++));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        try {
            initColor();
        }catch (Exception e){
            e.printStackTrace();
        }

        path=getIntent().getExtras().getString("mPicPath");
        imageId=getIntent().getExtras().getInt("mPicId");
        if (getIntent().getExtras().getString("mPicPath")!=null)
        {
            mPicSelected=BitmapFactory.decodeFile(path);
        }
        else mPicSelected=BitmapFactory.decodeResource(getResources(),imageId);
        Type=getIntent().getExtras().getInt("type",3);
        handleImage(mPicSelected);
        initView();
        generateGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameUtil.mBlankItemBean=new ItemBean();
        GameUtil.mItemBeans=new ArrayList<>();
        timer.cancel();
        timerTask.cancel();
        this.finish();
    }

    private void generateGame() {
        new ImagesUtil().createInitBitmaps(Type,mPicSelected,Puzzle.this);
        GameUtil.getPuzzleGenerator();
        for (ItemBean itemBean:GameUtil.mItemBeans){
            mPicSelectedList.add(itemBean.getmBitmap());
        }
        adapter=new GridPuzzleAdapter(Puzzle.this,mPicSelectedList);
        gridView.setAdapter(adapter);
        timer=new Timer(true);
        timerTask=new TimerTask() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=1;
                mHandle.sendMessage(msg);
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    private void initView() {
        timeText=(TextView)findViewById(R.id.time);
        index=(TextView)findViewById(R.id.index);
        oldImage=(Button)findViewById(R.id.oldImage);
        resetting=(Button)findViewById(R.id.resetting);
        back=(Button)findViewById(R.id.back);
        oldImage.setOnClickListener(this);
        resetting.setOnClickListener(this);
        back.setOnClickListener(this);
        flag=false;
        gridView=(GridView)findViewById(R.id.puzzle);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(mPicSelected.getWidth(),mPicSelected.getHeight());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,R.id.main_tlt);
        gridView.setLayoutParams(params);
        gridView.setNumColumns(Type);
        oldImage();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GameUtil.isMovable(position)){
                    GameUtil.swapItmes(GameUtil.mItemBeans.get(position),GameUtil.mBlankItemBean);
                    recreateDate();
                    adapter.notifyDataSetChanged();
                    COUNT_INDEX++;
                    index.setText(String.valueOf(COUNT_INDEX));
                    if (GameUtil.isSuccess()){
                        mPicSelectedList.remove(Type*Type-1);
                        mPicSelectedList.add(mLastBitmap);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(Puzzle.this,"拼图成功",Toast.LENGTH_SHORT).show();
                        gridView.setEnabled(false);
                        timer.cancel();
                        timerTask.cancel();
                    }
                }
            }
        });
    }

    private void recreateDate() {
        mPicSelectedList.clear();
        for (ItemBean itemBean:GameUtil.mItemBeans){
            mPicSelectedList.add(itemBean.getmBitmap());
        }
    }

    private void handleImage(Bitmap mPicSelected) {
        int ScreenWidth=ScreenUtil.getScreenSize(Puzzle.this).widthPixels;
        int ScreenHeight=ScreenUtil.getScreenSize(Puzzle.this).heightPixels;
        this.mPicSelected=ImagesUtil.resizeBitmap(ScreenWidth*0.9f,ScreenHeight*0.6f,mPicSelected);
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
    public void oldImage(){
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.activity_puzzle);
        imageView=new ImageView(Puzzle.this);
        imageView.setImageBitmap(mPicSelected);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(mPicSelected.getWidth(),mPicSelected.getHeight());
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(params);
        imageView.setVisibility(View.GONE);
        layout.addView(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:this.finish();
                break;
            case R.id.resetting:
                GameUtil.getPuzzleGenerator();
                recreateDate();
                adapter.notifyDataSetChanged();
                timer.cancel();
                timerTask.cancel();
                timer=new Timer(true);
                timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        Message msg=new Message();
                        msg.what=1;
                        mHandle.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask,0,1000);
                COUNT_INDEX=0;
                COUNT_TIME=0;
                timeText.setText("0");
                index.setText("0");
                gridView.setEnabled(true);
                break;
            case R.id.oldImage:
                Animation animShow= AnimationUtils.loadAnimation(Puzzle.this,R.anim.show);
                Animation animHide=AnimationUtils.loadAnimation(Puzzle.this,R.anim.hide);
                if (flag){
                    imageView.startAnimation(animHide);
                    imageView.setVisibility(View.GONE);
                    flag=false;
                    oldImage.setText("原图");
                }
                else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.startAnimation(animShow);
                    flag=true;
                    oldImage.setText("收起");
                }
                break;
        }
    }
}
