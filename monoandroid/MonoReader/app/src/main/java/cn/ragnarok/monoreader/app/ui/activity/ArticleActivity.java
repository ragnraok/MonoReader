package cn.ragnarok.monoreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.service.ArticleService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.view.ScrollableWebView;
import cn.ragnarok.monoreader.app.util.Utils;

public class ArticleActivity extends Activity {

    public static final String TAG ="Mono.ArticleActivity";
    public static final String ARTICLE_ID = "articleId";
    public static final String IS_FAV_ARTICLE = "isFavArticle";

    private ArticleObject mArticle;
    private int mArticleId;
    private boolean mIsFavArticle;
    private ArticleService mArticleService;
    private ScrollableWebView mWebView;
    private APIRequestFinishListener<ArticleObject> mRequestFinishListener;

    private boolean mIsInImmersive = false;

    private static final int SCROLL_THREADSHOLD = 100;
    private int mPreScrollY = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticleService = new ArticleService();

        setTitle("");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (ScrollableWebView) findViewById(R.id.article_views);


        mArticleId = getIntent().getIntExtra(ARTICLE_ID, -1);
        mIsFavArticle = getIntent().getBooleanExtra(IS_FAV_ARTICLE, false);

        initWebViewSetting();
        initRequestListener();
        loadArticleObject();
    }

    private void initWebViewSetting() {
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.setOnScrollChangeListener(new ScrollableWebView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(int currHoriScroll, int currVertiScroll, int oldHoriScroll, int oldVertiScroll) {
//                Log.d(TAG, "currVertiScroll: " + currVertiScroll + ", oldVertiScroll: " + oldVertiScroll);
                if (mPreScrollY == -1) {
                    mPreScrollY = currVertiScroll;
                    return;
                }
                if (currVertiScroll - mPreScrollY > SCROLL_THREADSHOLD) {
                    if (!mIsInImmersive) {
                        enterImmersive();
                    }
                    mPreScrollY = currVertiScroll;
                } else if (mPreScrollY - currVertiScroll > SCROLL_THREADSHOLD) {
                    if (mIsInImmersive) {
                        exitImmersive();
                    }
                    mPreScrollY = currVertiScroll;
                }
            }
        });

    }

    private void enterImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        getActionBar().hide();
        mIsInImmersive = true;
    }

    private void exitImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        getActionBar().show();
        mIsInImmersive = false;

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
        if (id == R.id.action_share) {
            shareArticle();
            return true;
        } else if (id == R.id.action_open_in_browser) {
            openInBrowser();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareArticle() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, mArticle.title);
        intent.putExtra(Intent.EXTRA_TEXT, mArticle.url);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void openInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArticle.url));
        startActivity(intent);
    }
}
