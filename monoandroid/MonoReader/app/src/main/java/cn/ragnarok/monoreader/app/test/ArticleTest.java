package cn.ragnarok.monoreader.app.test;

import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.object.ArticleObject;
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
                text.setText(articleObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                text.setText(volleyError.toString());
            }
        });
    }
}
