package com.github.callanna.housetelecontrol.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.github.callanna.housetelecontrol.R;
import com.github.callanna.metarialframe.util.BitmapUtil;
import com.github.callanna.metarialframe.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by Callanna on 2015/12/25.
 */
//定义继承BaseAdapter的匹配器
public class ImageAdapter extends BaseAdapter {

    //Item的修饰背景
    int mGalleryItemBackground;

    //上下文对象
    private Context mContext;
    ArrayList<BitmapDrawable> mBitmaps = new ArrayList<BitmapDrawable>();
    //图片数组
    private Integer[] mImageIds = { R.mipmap.folder_2antasy2,R.mipmap.folder_action,R.mipmap.folder_adventure ,
            R.mipmap.folder_animation,R.mipmap.folder_childrens,R.mipmap.folder_comedy,R.mipmap.folder_crime ,R.mipmap.folder_fantasy,
            R.mipmap.folder_musical,R.mipmap.folder_romance ,R.mipmap.folder_series,R.mipmap.folder_short};

    private void generateBitmaps()
    {
        for (int id : mImageIds)
        {
            Bitmap bitmap = createReflectedBitmapById(id);
            if (null != bitmap)
            {
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                drawable.setAntiAlias(true);
                mBitmaps.add(drawable);
            }
        }
    }

    private Bitmap createReflectedBitmapById(int resId)
    {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        if (drawable instanceof BitmapDrawable)
        {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            Bitmap reflectedBitmap = BitmapUtil.createReflectedBitmap(bitmap);
            return reflectedBitmap;
        }

        return null;
    }
    //构造方法
    public ImageAdapter(Context c){
        mContext = c;
        generateBitmaps();
    }
    public ImageAdapter(Context c,ArrayList<BitmapDrawable> mbitmaps){
        mContext = c;
        mBitmaps=mbitmaps;
        LogUtil.d("duanyl========>ImageAdapter  "+mBitmaps.size());
    }

    //返回项目数量
    @Override
    public int getCount() {
        return mBitmaps.size();
    }

    //返回项目
    @Override
    public Object getItem(int position) {
        return position;
    }

    //返回项目Id
    @Override
    public long getItemId(int position) {
        return position;
    }

    //返回视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            convertView = new ImageView(mContext);
            convertView.setLayoutParams(new Gallery.LayoutParams(150,200));
        }
        ImageView iv = (ImageView) convertView;
        iv.setImageDrawable(mBitmaps.get(position));
        return iv;
    }

}