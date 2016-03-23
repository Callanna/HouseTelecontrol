package com.github.callanna.housetelecontrol.mediaData;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.github.callanna.metarialframe.util.LogUtil;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2016/1/10.
 */
public class GetMediaData {

    public static List<Music> GetMusicData(Context c) {
        List<Music> MusicList = new ArrayList<Music>();
        ContentResolver cr = c.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        LogUtil.d("duanyl==========> cursor :" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                long songid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String suffix = name.substring(name.length() - 4, name.length());
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                long albumid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                // 获取缩略图
                Bitmap albumBitmap = getArtWork(c, songid, albumid, false);
                LogUtil.d("duanyl==========> cursor name :"+name );
                if (url.endsWith(".mp3") || url.endsWith(".MP3")) {
                    Music song = new Music();
                    song.setSongid(songid);
                    song.setName(name);
                    song.setTitle(title);
                    song.setUrl(url);
                    song.setTime(time);
                    song.setAlbum(album);
                    song.setAlbumBitmap(albumBitmap);
                    song.setAlbumid(albumid);
                    song.setSinger(singer);
                    song.setSize(size);
                    song.setSuffix(suffix);
                    MusicList.add(song);
                }
            }
        }
        return MusicList;
    }

    public static List<Music> GetVadioData(Context c) {
        List<Music> MusicList = new ArrayList<Music>();
        ContentResolver cr = c.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                long songid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String suffix = name.substring(name.length() - 4, name.length());
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                long albumid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                // 获取缩略图
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap albumBitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, songid, MediaStore.Images.Thumbnails.MICRO_KIND, options);
                if (url.endsWith(".mp4") || url.endsWith(".MP4")) {
//                    Music song = new Music();
//                    song.setSongid(songid);
//                    song.setName(name);
//                    song.setTitle(title);
//                    song.setUrl(url);
//                    song.setTime(time);
//                    song.setAlbum(album);
//                    song.setAlbumBitmap(albumBitmap);
//                    song.setAlbumid(albumid);
//                    song.setSinger(singer);
//                    song.setSize(size);
//                    song.setSuffix(suffix);
//                    MusicList.add(song);
                }
            }
        }
        return MusicList;
    }

    /**
     * ====================================================获得Mp3 专辑 图片
     */
    static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");
    static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    static Bitmap mCacheBit;

    /**
     * 从查询到的歌曲中获取专辑图片
     *
     * @param context      ：上下文对象
     * @param song_id      ：歌曲id
     * @param album_id     ：专辑id
     * @param allowdefault :boolean 变量
     * @return
     */
    public static Bitmap getArtWork(Context context, long song_id, long album_id, boolean allowdefault) {
        if (album_id < 0) {
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtWork(context);
            }
            return null;
        }
        ContentResolver cResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = cResolver.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (Exception e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtWork(context);
                        }
                    } else if (allowdefault) {
                        bm = getDefaultArtWork(context);
                    }
                    return bm;
                }
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }
        }
        return null;
    }

    /**
     * 设置默认图片
     *
     * @param context ：当前上下文对象
     * @return
     */
    private static Bitmap getDefaultArtWork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
//        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.music_imagetest), null,
//                opts);
        return null;
    }

    /**
     * 设置从歌曲中得到的专辑图片
     *
     * @param context  ：当前上下文对象
     * @param song_id  ：歌曲id(musicID)
     * @param album_id :专辑id(musicAlbum_ID)
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long song_id, long album_id) {
        Bitmap bm = null;
        if (album_id < 0 && song_id < 0) {
            throw new IllegalArgumentException("需要一个歌曲id或专辑id");
        }
        try {
            if (album_id < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + song_id + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (bm != null) {
            mCacheBit = bm;
        }
        return bm;
    }
}
