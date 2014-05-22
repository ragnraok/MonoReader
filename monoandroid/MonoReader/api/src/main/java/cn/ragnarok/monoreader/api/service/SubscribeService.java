package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-23.
 */
public class SubscribeService extends BaseAPIService {
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

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}
