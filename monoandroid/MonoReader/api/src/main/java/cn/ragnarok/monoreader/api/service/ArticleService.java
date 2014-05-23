package cn.ragnarok.monoreader.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIPostRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-22.
 */
public class ArticleService extends BaseAPIService {
    public static final String API_TAG = "Article";
    public static final String ARTICLE_OBJECT_DATA_KEY = "article";
    public static final String ARTICLE_LIST_DATA_KEY = "articles";

    public void loadArticle(int articleId, final APIRequestFinishListener<ArticleObject> requestFinishListener) {
        loaddArticleInternal(articleId, requestFinishListener, false);
    }

    public void loadFavArticle(int favArticleId, final APIRequestFinishListener<ArticleObject> requestFinishListener) {
        loaddArticleInternal(favArticleId, requestFinishListener, true);
    }

    private void loaddArticleInternal(int articleId, final APIRequestFinishListener<ArticleObject> requestFinishListener, boolean isLoadFav) {
        String url = null;
        if (isLoadFav) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_FAV_ARTICLE, articleId));
        } else {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_ARTICLE, articleId));
        }

        BaseAPIGetRequest request = new BaseAPIGetRequest(url, ARTICLE_OBJECT_DATA_KEY,
                requestFinishListener, new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Type articleType = new TypeToken<ArticleObject>() {}.getType();
                ArticleObject result = new Gson().fromJson(rawJson, articleType);
                if (requestFinishListener != null) {
                    requestFinishListener.onGetResult(result);
                }
            }
        });
        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void loadAllFavArticleList(final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        loadFavArticleListInternal(requestFinishListener, true, 0);
    }

    public void loadFavArticleList(int page, final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        loadFavArticleListInternal(requestFinishListener, false, page);
    }

    private void loadFavArticleListInternal(final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener, boolean isLoadAll, int page) {
        String url = null;
        if (!isLoadAll) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_FAV_ARTICLE_LIST, page));
        } else {
            url = APIService.getInstance().createURL(Constant.URL.LOAD_ALL_FAV_ARTICLE_LIST);
        }

        BaseAPIGetRequest request = new BaseAPIGetRequest(url, ARTICLE_LIST_DATA_KEY, requestFinishListener, new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Type resultType = new TypeToken<Collection<ListArticleObject>>() {}.getType();
                List<ListArticleObject> result = new Gson().fromJson(rawJson, resultType);
                if (requestFinishListener != null) {
                    requestFinishListener.onGetResult(result);
                }
            }
        });

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void favArticle(int articleId, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.FAV_ARTICLE);

        JSONObject data = new JSONObject();
        try {
            data.put("article_id", articleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseAPIPostRequest request = new BaseAPIPostRequest(url, data.toString(), requestFinishListener);

        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    public void unfavArticle(int articleId, APIRequestFinishListener requestFinishListener) {
        String url = APIService.getInstance().createURL(Constant.URL.UNFAV_ARTICLE);

        JSONObject data = new JSONObject();
        try {
            data.put("fav_article_id", articleId);
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