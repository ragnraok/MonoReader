package cn.ragnarok.monoreader.app.test;

import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.CategorySiteObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.SiteService;

/**
 * Created by ragnarok on 14-5-23.
 */
public class SiteTest {
    public static final String TAG = "Test.SiteTest";

    private SiteService service;
    private static SiteTest test;

    private SiteTest() {
        service = new SiteService();
    }

    public static SiteTest getTest() {
        if (test == null) {
            test = new SiteTest();
        }
        return test;
    }

    public void testLoadAllSiteArticleList(final TextView text, int siteId) {
        service.loadSiteAllArticleList(siteId, new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> result) {
                text.setText(result.size() + "\n");
                text.append(dumpArticleList(result));
            }
        });
    }

    public void testLoadSiteArticleList(final TextView text, int siteId, int page) {
        service.loadSiteArticleList(siteId, page, new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {


            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> result) {
                text.setText(result.size() + "\n");
                text.append(dumpArticleList(result));
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

    private String dumpSiteList(Collection<SiteObject> siteList) {
        StringBuilder result = new StringBuilder("");
        for (SiteObject site : siteList) {
            result.append(site.toString());
            result.append("\n");
        }
        return result.toString();
    }

    public void testLoadAllSite(final TextView text) {
        service.loadAllSite(new APIRequestFinishListener<Collection<SiteObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<SiteObject> result) {
                text.setText(result.size() + "\n");
                text.append(dumpSiteList(result));
            }
        });
    }

    public void testLoadAllSiteInCategory(final TextView text, String category) {
        service.loadAllSiteInCategory(category, new APIRequestFinishListener<Collection<SiteObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<SiteObject> result) {
                text.setText(result.size() + "\n");
                text.append(dumpSiteList(result));
            }
        });
    }

    public void testFavSite(final TextView text, int siteId) {
        service.favSite(siteId, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.setText("success");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

    public void testUnFavSite(final TextView text, int siteId) {
        service.unfavSite(siteId, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.setText("success");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

    public void testLoadAllSiteByCategory(final TextView text) {
        service.loadAllSiteByCategory(new APIRequestFinishListener<Collection<CategorySiteObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<CategorySiteObject> result) {
                text.setText(result.size() + "\n");
                text.append(dumpCategorSiteList(result));
            }
        });
    }

    private String dumpCategorSiteList(Collection<CategorySiteObject> result) {
        StringBuilder builder = new StringBuilder("");
        for (CategorySiteObject categorySiteObject : result) {
            builder.append(categorySiteObject.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
