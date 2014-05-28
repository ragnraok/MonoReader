package cn.ragnarok.monoreader.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.service.ArticleService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.util.Utils;

public class ArticleActivity extends Activity {

    public static final String TAG ="Mono.ArticleActivity";
    public static final String ARTICLE_ID = "articleId";
    public static final String IS_FAV_ARTICLE = "isFavArticle";

    private ArticleObject mArticle;
    private int mArticleId;
    private boolean mIsFavArticle;
    private ArticleService mArticleService;
    private WebView mWebView;
    private APIRequestFinishListener<ArticleObject> mRequestFinishListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticleService = new ArticleService();

        setTitle("");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.article_views);
        mArticleId = getIntent().getIntExtra(ARTICLE_ID, -1);
        mIsFavArticle = getIntent().getBooleanExtra(IS_FAV_ARTICLE, false);

        initWebViewSetting();
        initRequestListener();
        loadArticleObject();
    }

    private void initWebViewSetting() {
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
    }

    private void initRequestListener() {
        mRequestFinishListener = new APIRequestFinishListener<ArticleObject>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Toast.makeText(ArticleActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetResult(ArticleObject result) {
                mArticle = result;
                loadArticleHtml();
            }
        };
    }

    private void loadArticleObject() {
        if (mArticleId != -1) {
            if (mIsFavArticle) {
                mArticleService.loadFavArticle(mArticleId, mRequestFinishListener);
            } else {
                mArticleService.loadArticle(mArticleId, mRequestFinishListener);
            }
        }
    }

    private void loadArticleHtml() {
        if (mArticle != null) {
            mWebView.loadDataWithBaseURL(Utils.ASSET_DIR,
                    String.format(Utils.ARTICLE_HTML_FORMAT, mArticle.title, mArticle.site, mArticle.updated, mArticle.content),
                    "text/html", "UTF-8", null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
