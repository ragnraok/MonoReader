package cn.ragnarok.monoreader.api.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.util.ArrayList;

/**
 * Created by ragnarok on 14-5-21.
 */
public class APIService {
    private RequestQueue mRequestQueue;
    private Context mContext;
    private boolean mIsInit;
    private String mHost; // must end with tail slash

    private static ArrayList<String> serviceTagList = new ArrayList<String>();
    static {
        serviceTagList.add(TimeLineService.API_TAG);
        serviceTagList.add(ArticleService.API_TAG);
        serviceTagList.add(SubscribeService.API_TAG);
    }

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
    public static void init(Context context, String host, RequestQueue requestQueue) {
        if (service == null) {
            service = new APIService();
        }
        service.mContext = context;
        service.mRequestQueue = requestQueue;
        service.mIsInit = true;
        service.mHost = host;
    }

    void queueJob(Request request) {
        this.mRequestQueue.add(request);
    }

    public RequestQueue getQueue() {
        return mRequestQueue;
    }

    public void cancelAllRequest() {
        for (String tag : serviceTagList) {
            this.mRequestQueue.cancelAll(tag);
        }
    }

    public String host() { return mHost; }

    String createURL(String url) {
        return mHost + url;
    }

}