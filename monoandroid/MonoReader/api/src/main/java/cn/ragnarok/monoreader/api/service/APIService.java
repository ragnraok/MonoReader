package cn.ragnarok.monoreader.api.service;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Created by ragnarok on 14-5-21.
 */
public class APIService {
    private RequestQueue mRequestQueue;
    private Context mContext;
    private boolean mIsInit;

    private static APIService service;
    private APIService() {}

    public static APIService getInstance() {
        if (service == null) {
            service = new APIService();
            service.mIsInit = false;
        }
        return service;
    }

    public boolean isInit() {
        return this.mIsInit;
    }

    /**
     * init the API Service, call when application start
     * @param context
     * @param requestQueue
     */
    public static void init(Context context, RequestQueue requestQueue) {
        if (service == null) {
            service = new APIService();
        }
        service.mContext = context;
        service.mRequestQueue = requestQueue;
        service.mIsInit = true;
    }

}
