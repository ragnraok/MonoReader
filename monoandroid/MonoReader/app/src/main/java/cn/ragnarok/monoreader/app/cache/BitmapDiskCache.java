package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ragnarok on 14-5-26.
 */
public class BitmapDiskCache {

    public static final String TAG = "Mono.BitmapDiskCache";
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_KEY_LENGTH = 128;
    private static final String CACHE_DIR = "MonoImageCache";
    private static int MAX_CACHE_SIZE = 20;
    private File mCacheDir;

    private ConcurrentHashMap<String, String> mCacheMap; // key, file
    private LruCache<String, SoftReference<Bitmap>> mInMemoryCache; // url, bitmap, speed up

    private static BitmapDiskCache mInstance = null;
    private HandlerThread mPutCacheThread = new HandlerThread("cache", Thread.MIN_PRIORITY);
    private Handler mPutCacheHandler = null;

    public static BitmapDiskCache getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BitmapDiskCache(context);
        }
        return mInstance;
    }

    public static void init(Context context, int maxSize) {
        MAX_CACHE_SIZE = maxSize;
        if (mInstance == null) {
            mInstance = new BitmapDiskCache(context);
        }

    }

    private BitmapDiskCache(Context context) {

        mCacheDir = new File(context.getExternalFilesDir(CACHE_DIR).getPath());
        mCacheMap = new ConcurrentHashMap<String, String>();

        // put all exist cache
        File[] allCahce = mCacheDir.listFiles();
        for (File f : allCahce) {
            mCacheMap.put(f.getName(), f.getAbsolutePath());
        }

        mInMemoryCache = new LruCache<String, SoftReference<Bitmap>>(MAX_CACHE_SIZE);

        mPutCacheThread.start();
        mPutCacheHandler = new Handler(mPutCacheThread.getLooper());

    }

    private String toMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    private String getKey(String url) {
        String key = toMD5(url);
        if (key.length() > MAX_KEY_LENGTH) {
            key = key.substring(0, MAX_KEY_LENGTH);
        }
        return key;
    }

    public void put(final String url, final Bitmap bitmap) {

        final String key = getKey(url);

        if (bitmap != null && !mCacheMap.containsKey(key)) {

            mInMemoryCache.put(key, new SoftReference<Bitmap>(bitmap));

            mPutCacheHandler.post(new Runnable() {
                @Override
                public void run() {
                    File newCache = new File(mCacheDir + File.separator + key);
                    FileOutputStream fos = null;
                    try {
                        newCache.createNewFile();
                        fos = new FileOutputStream(newCache);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        mCacheMap.put(key, newCache.getAbsolutePath());
                        Log.d(TAG, "put bitmap, url: " + url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

    }

    public Bitmap get(String url) {
        Bitmap bitmap = null;
        String key = getKey(url);
        if (mInMemoryCache.get(key) != null) {
            SoftReference<Bitmap> softRefBitmap = mInMemoryCache.get(key);
            bitmap = softRefBitmap.get();
            if (bitmap != null) {
                Log.d(TAG, "disk cache hit in memory, url=" + url);
                return bitmap;
            }
        }
        if (bitmap == null) {
            if (mCacheMap.containsKey(key)) {
                String path = mCacheMap.get(key);
                bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    Log.d(TAG, "disk cache hit, url=" + url);
                    mInMemoryCache.put(key, new SoftReference<Bitmap>(bitmap));
                    return bitmap;
                }
            }

        }
        Log.d(TAG, "disk cache not hit, url = " + url);
        return null;
    }



    public boolean exist(String url) {
        return mCacheMap.containsKey(getKey(url));
    }
}
