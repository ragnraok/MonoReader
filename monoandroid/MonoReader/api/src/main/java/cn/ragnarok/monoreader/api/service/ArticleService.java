package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-22.
 */
public class ArticleService {
    public static final String API_TAG = "Article";
    public static final String ARTICLE_OBJECT_DATA_KEY = "article";
    public static final String ARTICLE_LIST_DATA_KEY = "articles";

    public void loadArticle(int articleId, final APIResultListener<ArticleObject> resultListener,
                            final Response.ErrorListener errorListener) {
        loaddArticleInternal(articleId, resultListener, errorListener, false);
    }

    public void loadFavArticle(int favArticleId, final APIResultListener<ArticleObject> resultListener,
                               final Response.ErrorListener errorListener) {
        loaddArticleInternal(favArticleId, resultListener, errorListener, true);
    }

    private void loaddArticleInternal(int articleId, final APIResultListener<ArticleObject> resultListener,
                                     final Response.ErrorListener errorListener, boolean isLoadFav) {
        String url = null;
        if (isLoadFav) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_FAV_ARTICLE, articleId));
        } else {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_ARTICLE, articleId));
        }

        BaseAPIGetRequest request = new BaseAPIGetRequest(url, ARTICLE_OBJECT_DATA_KEY,
                errorListener, new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Type articleType = new TypeToken<ArticleObject>() {}.getType();
                ArticleObject result = new Gson().fromJson(rawJson, articleType);
                if (resultListener != null) {
                    resultListener.onResultGet(result);
                }
            }
        });
        request.get().setTag(API_TAG);
        if (APIService.getInstance().isInit()) {
            APIService.getInstance().queueJob(request.get());
        }
    }

    public void loadAllFavArticleList(final APIResultListener<List<ArticleObject>> resultListener,
                                         final Response.ErrorListener errorListener) {
        loadFavArticleListInternal(resultListener, errorListener, false, 0);
    }

    public void loadFavArticleList(int page, final APIResultListener<List<ArticleObject>> resultListener,
                                   final Response.ErrorListener errorListener) {
        loadFavArticleListInternal(resultListener, errorListener, true, page);
    }

    private void loadFavArticleListInternal(final APIResultListener<List<ArticleObject>> resultListener,
                                            final Response.ErrorListener errorListener, boolean isLoadAll, int page) {
        String url = null;
        if (!isLoadAll) {
            url = APIService.getInstance().createURL(String.format(Constant.URL.LOAD_FAV_ARTICLE_LIST, page));
        } else {
            url = APIService.getInstance().createURL(Constant.URL.LOAD_ALL_FAV_ARTICLE_LIST);
        }

        BaseAPIGetRequest request = new BaseAPIGetRequest(url, ARTICLE_LIST_DATA_KEY, errorListener, new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Type resultType = new TypeToken<List<ArticleObject>>() {}.getType();
                List<ArticleObject> result = new Gson().fromJson(rawJson, resultType);
                if (resultListener != null) {
                    resultListener.onResultGet(result);
                }
            }
        });

        if (APIService.getInstance().isInit()) {
            APIService.getInstance().queueJob(request.get());
        }
    }
}
