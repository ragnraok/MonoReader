package cn.ragnarok.monoreader.api.service;

import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-23.
 */
public class SubscribeService extends BaseAPIService {
    public static final String TAG = "SubscribeService";
    public static final String API_TAG = "subscribe";

    public void subscribe(String title, String siteUrl, String category, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.SUBSCRIBE);

        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("site_url", siteUrl);
            data.put("category", category);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);
        request.get().setTag(API_TAG);

        APIService.getInstance().queueJob(request.get());
    }

    public void unsubscribe(int siteId, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.UNSUBSCRIBE);

        JSONObject data = new JSONObject();
        try {
            data.put("site_id", siteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);
        request.get().setTag(API_TAG);

        APIService.getInstance().queueJob(request.get());
    }

    public void bundleUnSubscribe(List<Integer> siteIds, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.BUNDLE_UNSUBSCRIBE);

        JSONObject data = new JSONObject();
        try {
            data.put("site_ids", new JSONArray(siteIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        SiteIdList siteIdList = new SiteIdList();
//        siteIdList.site_ids = siteIds;
//        String data = new Gson().toJson(siteIdList, SiteIdList.class);
        Log.d(TAG, "bundleUnSubscribe, data: " + data.toString());
        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);
        request.get().setTag(API_TAG);

        APIService.getInstance().queueJob(request.get());
    }

//    class SiteIdList {
//        List<Integer> site_ids;
//    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}
