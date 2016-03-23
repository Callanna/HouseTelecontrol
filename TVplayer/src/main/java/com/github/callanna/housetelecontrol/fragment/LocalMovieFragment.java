package com.github.callanna.housetelecontrol.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.github.callanna.housetelecontrol.Constants;
import com.github.callanna.housetelecontrol.R;
import com.github.callanna.housetelecontrol.TVAppliction;
import com.github.callanna.housetelecontrol.activity.VideoPlayerAtviity;
import com.github.callanna.housetelecontrol.adapter.ImageAdapter;
import com.github.callanna.housetelecontrol.data.ImageManager;
import com.github.callanna.housetelecontrol.data.MediaItem;
import com.github.callanna.housetelecontrol.data.VideoList;
import com.github.callanna.housetelecontrol.data.VideoObject;
import com.github.callanna.housetelecontrol.data.VideoWorkItem;
import com.github.callanna.housetelecontrol.view.GalleryFlow;
import com.github.callanna.metarialframe.base.BaseFragment;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.SdCardUtil;

import java.util.ArrayList;
import java.util.List;

import RxJava.RxBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Callanna on 2015/12/25.
 */
public class LocalMovieFragment extends BaseFragment {
    private GalleryFlow galleryFlow ;
    private VideoList mAllImages;
    private List<VideoWorkItem> mAllVideoList = new ArrayList<VideoWorkItem>();
    ArrayList<BitmapDrawable> mBitmaps = new ArrayList<BitmapDrawable>();
    ArrayList<MediaItem>  mCurrentPlayList = new ArrayList<MediaItem>();
    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.fragment_localmovie);
        TVAppliction.nowPage = Constants.MoviePage;
        Observable<String> OK = RxBus.get().register("OK",String.class);
        OK.observeOn(AndroidSchedulers.mainThread()).subscribe(obserOK);
        if (SdCardUtil.isSdCardAvailable()) {
            Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    getVideoData();
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
                @Override
                public void onCompleted() {
                    initGallery();
                }
                @Override
                public void onError(Throwable e) {
                }
                @Override
                public void onNext(Object o) {
                }
            });
        }

    }
    Observer<String> obserOK = new Observer<String>() {
        @Override
        public void onCompleted() {
        }
        @Override
        public void onError(Throwable e) {
        }
        @Override
        public void onNext(String s) {
            if(TVAppliction.nowPage.equals(Constants.MoviePage)&&mCurrentPlayList.size()>0) {
                Intent intent = new Intent(context, VideoPlayerAtviity.class);
                intent.putExtra("position",0);
                intent.putExtra("playlist",mCurrentPlayList);
                context.startActivity(intent);
            }
        }
    };
    private void initGallery() {
        galleryFlow = (GalleryFlow) findViewById(R.id.gallery);
        galleryFlow.setBackgroundColor(getResources().getColor(R.color.gray_tranpart));
        galleryFlow.setFadingEdgeLength(0);
        galleryFlow.setGravity(Gravity.CENTER_VERTICAL);
        galleryFlow.setAdapter(new ImageAdapter(context, mBitmaps));
        galleryFlow.setSpacing(10);
        galleryFlow.setMaxZoom(-120);
        galleryFlow.setMaxRotationAngle(60);
        galleryFlow.setSelection(3);
        galleryFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, VideoPlayerAtviity.class);
                intent.putExtra("position",i);
                intent.putExtra("playlist",mCurrentPlayList);
                context.startActivity(intent);
            }
        });
    }

    private VideoList allImages() {
        mAllImages = null;
        return ImageManager.instance().allImages(context, context.getContentResolver(),
                ImageManager.INCLUDE_VIDEOS, ImageManager.SORT_ASCENDING);
    }

    public void getVideoData() {
        mAllVideoList.clear();
        mAllImages = allImages(); // Video List
        LogUtil.d("duanyl=======+mAllImages  " +mAllImages.getCount());
        if (mAllImages != null) {
            int totalNum = mAllImages.getCount();
            for (int i = 0; i < totalNum; i++) {
                VideoObject image = mAllImages.getImageAt(i);

                VideoWorkItem videoDisplayObj = new VideoWorkItem();
                videoDisplayObj.object = image;
                videoDisplayObj.name = image.getTitle();
                videoDisplayObj.duration = getString(R.string.duration_tag)
                        + " " + image.getDuration();
                videoDisplayObj.size = image.getSize();
                videoDisplayObj.datapath = image.getMediapath();
                mAllVideoList.add(videoDisplayObj);
            }
            LogUtil.d("duanyl=======+mAllVideoList  " +mAllVideoList.size());
            generateBitmaps();
            generatePlayList();

        }
    }
    private void generateBitmaps()
    {
        LogUtil.d("duanyl=======+generateBitmaps");
        for (int i = 0;i < mAllImages.getCount();i++)
        {
            Bitmap bitmap = mAllImages.getImageAt(i).miniThumbBitmap(false, null, BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.folder_animation));
            if (null != bitmap)
            {
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                drawable.setAntiAlias(true);
                mBitmaps.add(drawable);
            }
        }
        LogUtil.d("duanyl=======+generateBitmaps  " +mBitmaps.size());
    }
    private void generatePlayList() {
        LogUtil.d("duanyl=======+generatePlayList");
        int i = 0;
        while (i < mAllVideoList.size()) {
            MediaItem info = new MediaItem();
            info.setTitle(((VideoWorkItem) mAllVideoList.get(i)).name);
            info.setUrl(((VideoWorkItem) mAllVideoList.get(i)).datapath);
            mCurrentPlayList.add(info);
            i++;
        }
        LogUtil.d("duanyl=======+generatePlayList  " + mCurrentPlayList.size());
    }

}
