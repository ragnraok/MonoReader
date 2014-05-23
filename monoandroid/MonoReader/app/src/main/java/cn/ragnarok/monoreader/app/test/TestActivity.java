package cn.ragnarok.monoreader.app.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.List;

import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.api.service.ArticleService;
import cn.ragnarok.monoreader.api.service.TimeLineService;
import cn.ragnarok.monoreader.app.R;


public class TestActivity extends Activity {

    public static final String TAG = "APITest";
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        prepareTest();
//        TimelineTest.getTest().testFavTimeline(text, 1);

//        ArticleTest.getTest().testLoadArticle(text, 30);
//        ArticleTest.getTest().testLoadFavArticle(text, 1);
//        ArticleTest.getTest().testLoadAllFavArticleList(text);
//        ArticleTest.getTest().testLoadFavArticleList(text, 2);
//        ArticleTest.getTest().testFavArticle(text, 30);
//        ArticleTest.getTest().testUnfavArticle(text, 5);
//
//        SubscribeTest.getTest().testSubscribe(text, null, "http://www.solidot.org/", null);
//        SubscribeTest.getTest().testUnsubscribe(text, 8);

//        CategoryTest.getTest().testGetAllCategory(text);
//        CategoryTest.getTest().testSetCategory(text, 6, "test");
        CategoryTest.getTest().testUnsetCategory(text, 6);
    }

    public void prepareTest() {
        if (!APIService.getInstance().isInit()) {
            APIService.init(this, TestUtil.HOST, Volley.newRequestQueue(this));
        }
    }
}
