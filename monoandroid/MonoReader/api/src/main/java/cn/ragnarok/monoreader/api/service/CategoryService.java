package cn.ragnarok.monoreader.api.service;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.CategoryObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-23.
 */
public class CategoryService extends BaseAPIService {

    public static final String API_TAG = "category";
    public static final String DATA_KEY = "category";
    public static final String CATEGORY_TIMELINE_DATA_KEY = "articles";
    public static final String CATEGORY_TIMELINE_TAG = "categoryTimeline";


    public void loadAllCategoryList(final APIRequestFinishListener<Collection<CategoryObject>> requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.GET_ALL_CATEGORY);
        Type resultType = new TypeToken<Collection<CategoryObject>>() {}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, DATA_KEY, resultType, requestFinishListener);
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

    public void categoryTimeline(String category, int page, APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        categoryTimelineInternal(false, category, page, requestFinishListener);
    }

    public void unclassifiedCategoryTimeline(int page, APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        categoryTimelineInternal(true, null, page, requestFinishListener);
    }

    private void categoryTimelineInternal(boolean isUnClassified, String category, int page,
                                          APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        String url = null;
        if (isUnClassified) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.UNCLASSIFIED_CATEGORY_TIMELINE, page));
        } else {
            try {
                url = APIService.getInstance().createURL(String.format(Constant.URL.CATEGORY_TIMELINE,
                        URLEncoder.encode(category, "UTF-8"), page));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Type resultType = new TypeToken<Collection<ListArticleObject>>(){}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, CATEGORY_TIMELINE_DATA_KEY, resultType, requestFinishListener);
        request.get().setTag(CATEGORY_TIMELINE_TAG);

        APIService.getInstance().queueJob(request.get());
    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }

    public void cancelCategoryTimelineRequest() {
        APIService.getInstance().getQueue().cancelAll(CATEGORY_TIMELINE_TAG);
    }
}
