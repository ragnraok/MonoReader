package cn.ragnarok.monoreader.app.test;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
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
        service.loadArticle(articleId, new APIRequestFinishListener<ArticleObject>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(ArticleObject articleObject) {
                if (articleObject == null) {
                    text.setText("");
                } else {
                    text.setText(articleObject.toString());
                }
            }
        });
    }

    public void testLoadFavArticle(final TextView text, final int favArticleId) {
        service.loadFavArticle(favArticleId, new APIRequestFinishListener<ArticleObject>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(ArticleObject articleObject) {
                if (articleObject == null) {
                    text.setText("");
                } else {
                    text.setText(articleObject.toString());
                }
            }
        });
    }

    public void testLoadAllFavArticleList(final TextView text) {
        service.loadAllFavArticleList(new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> articleObjects) {
                if (articleObjects == null) {
                    text.setText("");
                } else {
                    text.setText(articleObjects.size() + "\n");
                    text.append(dumpArticleList(articleObjects));
                }
            }
        });
    }

    public void testLoadFavArticleList(final TextView text, int page) {
        service.loadFavArticleList(page, new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> articleObjects) {
                if (articleObjects == null) {
                    text.setText("");
                } else {
                    text.setText(articleObjects.size() + "\n");
                    text.append(dumpArticleList(articleObjects));
                }
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

    public void testFavArticle(final TextView text, int articleId) {
       text.setText("");
       service.favArticle(articleId, new APIRequestFinishListener() {

           @Override
           public void onRequestSuccess() {
                text.setText("success\n");
           }

           @Override
           public void onRequestFail(VolleyError error) {
               text.setText(error.toString() + "\n");
           }

           @Override
           public void onGetResult(Object result) {

           }
       });
    }

    public void testUnfavArticle(final TextView text, int articleId) {
        service.unfavArticle(articleId, new APIRequestFinishListener() {

            @Override
            public void onRequestSuccess() {
                text.setText("success\n");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString() + "\n");
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

}
