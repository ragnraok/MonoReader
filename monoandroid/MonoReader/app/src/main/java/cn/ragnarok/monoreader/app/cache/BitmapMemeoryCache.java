package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ragnarok on 14-5-26.
 */
public class BitmapMemeoryCache extends LruCache<String, SoftReference<Bitmap>> implements ImageLoader.ImageCache {

    public static final String TAG = "Mono.BitmapMemeoryCache";

    public BitmapMemeoryCache(Context context, int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, SoftReference<Bitmap> value) {
        Bitmap bitmap = value.get();
        if (bitmap != null) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
        return super.sizeOf(key, value);

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

        return bitmap;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        if (bitmap != null) {
            put(s, new SoftReference<Bitmap>(bitmap));
        }

    }
}
