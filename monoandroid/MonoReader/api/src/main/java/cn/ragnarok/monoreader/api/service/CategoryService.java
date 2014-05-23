package cn.ragnarok.monoreader.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.CategoryObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-23.
 */
public class CategoryService extends BaseAPIService {

    public static final String API_TAG = "category";
    public static final String DATA_KEY = "category";


    public void loadAllCategoryList(final APIRequestFinishListener<Collection<CategoryObject>> requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.GET_ALL_CATEGORY);
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, DATA_KEY, requestFinishListener, new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Gson gson = new Gson();
                Type resultType = new TypeToken<Collection<CategoryObject>>() {}.getType();
                Collection<CategoryObject> categoryList = gson.fromJson(rawJson, resultType);
                if (requestFinishListener != null) {
                    requestFinishListener.onGetResult(categoryList);
                }
            }
        });
        request.get().setTag(API_TAG);

        APIService.getInstance().queueJob(request.get());
    }

    public void setSiteCategory(int siteId, String category, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.SET_CATEGORY);

        JSONObject data = new JSONObject();
        try {
            data.put("site_id", siteId);
            data.put("category", category);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);
        request.get().setTag(API_TAG);

        APIService.getInstance().queueJob(request.get());
    }

    public void unsetSiteCategory(int siteId, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.UNSET_CATEGORY);

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
