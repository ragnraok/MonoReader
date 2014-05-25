package cn.ragnarok.monoreader.app.base;

import android.app.Application;

import com.android.volley.toolbox.Volley;

import cn.ragnarok.monoreader.api.service.APIService;

/**
 * Created by ragnarok on 14-5-25.
 */
public class MonoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        APIService.init(this, Volley.newRequestQueue(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
