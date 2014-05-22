package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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
        String url = String.format(Constant.URL.LOAD_ARTICLE, articleId);
        url = APIService.getInstance().createURL(url);
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
}
