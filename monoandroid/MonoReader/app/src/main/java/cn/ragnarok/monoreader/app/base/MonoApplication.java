package cn.ragnarok.monoreader.app.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.Volley;

import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.cache.BitmapDiskCache;

/**
 * Created by ragnarok on 14-5-25.
 */
public class MonoApplication extends Application {

    private static final String TAG = "Mono.MonoApplication";
    private static final int MAX_CACHE_ITEM_NUM = 20;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate Application");
        APIService.init(this, Volley.newRequestQueue(this));

        BitmapDiskCache.init(this, MAX_CACHE_ITEM_NUM);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate Application");
        APIService.getInstance().cancelAllRequest();
    }
}
