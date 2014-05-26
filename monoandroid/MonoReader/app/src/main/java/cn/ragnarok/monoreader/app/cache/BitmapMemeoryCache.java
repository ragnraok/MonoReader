package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.lang.ref.SoftReference;

/**
 * Created by ragnarok on 14-5-26.
 */
public class BitmapMemeoryCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public static final String TAG = "Mono.BitmapMemeoryCache";

    public BitmapMemeoryCache(Context context, int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {

        if (bitmap != null) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
        return 0;

    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    @Override
    public Bitmap getBitmap(String s) {
        Log.d(TAG, "try to getBitmap, key = " + s);
        Bitmap bitmap = get(s);

        if (bitmap != null) {
            Log.d(TAG, "cache hit for key " + s);
        }


        return bitmap;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        if (bitmap != null) {
            put(s, bitmap);
            Log.d(TAG, "putBitmap for key " + s);
        }

    }
}
