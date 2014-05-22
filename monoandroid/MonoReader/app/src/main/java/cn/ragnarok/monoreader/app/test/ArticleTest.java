package cn.ragnarok.monoreader.app.test;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.ArticleService;

/**
 * Created by ragnarok on 14-5-22.
 */
public class ArticleTest {
    public static final String TAG = "Test.ArticleTest";

    private ArticleService service = null;

    private static ArticleTest test = null;
    private ArticleTest() {
        service = new ArticleService();
    }

    public static ArticleTest getTest() {
        if (test == null) {
            test = new ArticleTest();
        }
        return test;
    }

    public void testLoadArticle(final TextView text, final int articleId) {
        service.loadArticle(articleId, new APIResultListener<ArticleObject>() {
            @Override
            public void onResultGet(ArticleObject articleObject) {
                if (articleObject == null) {
                    text.setText("");
                } else {
                    text.setText(articleObject.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }
        });
    }

    public void testLoadFavArticle(final TextView text, final int favArticleId) {
        service.loadFavArticle(favArticleId, new APIResultListener<ArticleObject>() {
            @Override
            public void onResultGet(ArticleObject articleObject) {
                if (articleObject == null) {
                    text.setText("");
                } else {
                    text.setText(articleObject.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }
        });
    }

    public void testLoadAllFavArticleList(final TextView text) {
        service.loadAllFavArticleList(new APIResultListener<Collection<ListArticleObject>>() {
            @Override
            public void onResultGet(Collection<ListArticleObject> articleObjects) {
                if (articleObjects == null) {
                    text.setText("");
                } else {
                    text.setText(articleObjects.size() + "\n");
                    text.append(dumpArticleList(articleObjects));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }
        });
    }

    public void testLoadFavArticleList(final TextView text, int page) {
        service.loadFavArticleList(page, new APIResultListener<Collection<ListArticleObject>>() {
            @Override
            public void onResultGet(Collection<ListArticleObject> listArticleObjects) {
                Log.d(TAG, listArticleObjects.toString() + " " + listArticleObjects.size());
                if (listArticleObjects == null) {
                    text.setText("");
                } else {
                    text.setText(listArticleObjects.size() + "\n");
                    text.append(dumpArticleList(listArticleObjects));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }
        });
    }

    private String dumpArticleList(Collection<ListArticleObject> articleList) {
        StringBuilder result = new StringBuilder("");
        for (ListArticleObject article : articleList) {
            result.append(article.toString());
            result.append("\n");
        }
        return result.toString();
    }

}
