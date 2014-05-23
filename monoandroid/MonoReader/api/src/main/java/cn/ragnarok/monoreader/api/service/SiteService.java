package cn.ragnarok.monoreader.api.service;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.lang.reflect.Type;
import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.CategorySiteObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-23.
 */
public class SiteService extends BaseAPIService {

    public static final String API_TAG = "site";
    public static final String SITE_ARTICLE_DATA_KEY = "articles";
    public static final String SITE_LIST_DATA_KEY = "sites";

    public void loadSiteAllArticleList(int siteId,
                                    final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        loadSiteArticleInternal(true, siteId, 0, requestFinishListener);
    }

    public void loadSiteArticleList(int siteId, int page,
                                    final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        loadSiteArticleInternal(false, siteId, page, requestFinishListener);
    }

    private void loadSiteArticleInternal(boolean isLoadAll, int siteId, int page,
                                         final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        String url = null;
        if (isLoadAll) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_SITE_ALL_ARTICLE, siteId));
        } else {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_SITE_ARTICLE, siteId, page));
        }

        Type resultType = new TypeToken<Collection<ListArticleObject>>(){}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, SITE_ARTICLE_DATA_KEY, resultType, requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void loadAllSite(APIRequestFinishListener<Collection<SiteObject>> requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.LOAD_ALL_SITE);

        Type resultType = new TypeToken<Collection<SiteObject>>() {}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, SITE_LIST_DATA_KEY, resultType, requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void loadAllSiteInCategory(String category, APIRequestFinishListener<Collection<SiteObject>> requestFinishListener)  {
        String url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_ALL_SITE_IN_CATEGORY, category));

        Type resultType = new TypeToken<Collection<SiteObject>>(){}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, SITE_LIST_DATA_KEY, resultType, requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void favSite(int siteId, APIRequestFinishListener requestFinishListener) {
        favUnfavSiteInternal(true, siteId, requestFinishListener);
    }

    public void unfavSite(int siteId, APIRequestFinishListener requestFinishListener) {
        favUnfavSiteInternal(false, siteId, requestFinishListener);
    }

    private void favUnfavSiteInternal(boolean isFav, int siteId, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.FAV_STIE);
        JSONObject data = new JSONObject();
        try {
            data.put("site_id", siteId);
            data.put("is_fav", isFav);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void loadAllSiteByCategory(final APIRequestFinishListener<Collection<CategorySiteObject>> requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.LOAD_ALL_SITE_BY_CATEGORY);

        Type resultType = new TypeToken<Collection<CategorySiteObject>>(){}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, SITE_LIST_DATA_KEY, resultType, requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}
