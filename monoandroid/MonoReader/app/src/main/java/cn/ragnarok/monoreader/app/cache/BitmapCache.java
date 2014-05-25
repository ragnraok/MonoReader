package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * Created by ragnarok on 14-5-26.
 */
public class BitmapCache extends LruCache<String, SoftReference<Bitmap>> implements ImageLoader.ImageCache {

    private static final String TAG = "Mono.BitmapCache";

    private DiskBasedCache mBitmapDiskCache;

    public BitmapCache(Context context, int maxSize) {
        super(maxSize);
        mBitmapDiskCache = new DiskBasedCache(context.getCacheDir());
    }

    @Override
    protected int sizeOf(String key, SoftReference<Bitmap> value) {
        Bitmap bitmap = value.get();
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, SoftReference<Bitmap> oldValue, SoftReference<Bitmap> newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    @Override
    public Bitmap getBitmap(String s) {
        SoftReference<Bitmap> softRefBitmap = get(s);
        Bitmap bitmap = null;
        if (softRefBitmap != null) {
            bitmap = softRefBitmap.get();
        }
        if (bitmap == null) {
            bitmap = getFromDiskCache(s);
        }
        if (bitmap != null) {
            put(s, new SoftReference<Bitmap>(bitmap));
        }
        return bitmap;
    }

    private Bitmap getFromDiskCache(String key) {
        Log.d(TAG, "get cache from disk, key=" + key);
        Cache.Entry entry = mBitmapDiskCache.get(key);
        if (entry != null) {
            Bitmap result = BitmapFactory.decodeByteArray(entry.data, 0, entry.data.length);
            return result;
        }
        return null;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        put(s, new SoftReference<Bitmap>(bitmap));

        putDiskCache(s, bitmap);

    }

    private void putDiskCache(String key, Bitmap bitmap) {
        Log.d(TAG, "put bitmap to disk, key=" + key);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Cache.Entry entry = new Cache.Entry();
        entry.data = byteArray;
        mBitmapDiskCache.put(key, entry);

    }
}
