package cn.ragnarok.monoreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
    public static final String IS_FROM_FAV_ARTICLE_LIST = "isFromArticleList";
    public static final String IS_FAV_ARTICLE = "isFavArticle";

    private ArticleObject mArticle;
    private int mArticleId;
    private boolean mIsFavArticle;
    private boolean mIsFromFavArticleList;
    private ArticleService mArticleService;
    private ScrollableWebView mWebView;
    private View mMainLayout;
    private APIRequestFinishListener<ArticleObject> mLoadArticleListener;
    private APIRequestFinishListener mFavListener;
    private MenuItem mFavItem;
    private boolean mIsInImmersive = false;
    private boolean mIsInFavProcess = false;

    private static final int SCROLL_THREADSHOLD = 100;
    private int mPreScrollY = -1;

    private Handler mWebViewClickHandler = null;
    private static final int CLICK_DELAY_MESSAGE = 1;
    private static final int CLICK_TIMEOUT = 300;

    public static final int FAV_SET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_article);

        mArticleService = new ArticleService();

        setTitle("");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (ScrollableWebView) findViewById(R.id.article_views);
        mMainLayout = findViewById(R.id.article_layout);

        mArticleId = getIntent().getIntExtra(ARTICLE_ID, -1);
        mIsFromFavArticleList = getIntent().getBooleanExtra(IS_FROM_FAV_ARTICLE_LIST, false);

        setProgressBarIndeterminate(true);


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
                mWebViewClickHandler.removeMessages(CLICK_DELAY_MESSAGE);
                if (mPreScrollY == -1) {
                    mPreScrollY = currVertiScroll;
                    return;
                }
                if (currVertiScroll >=  Math.floor(mWebView.getContentHeight() * mWebView.getScale())) { // detect if in bottom
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

        mWebViewClickHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    //Toast.makeText(ArticleActivity.this, "click Webview", Toast.LENGTH_SHORT).show();
                    mWebViewClickHandler.removeMessages(CLICK_DELAY_MESSAGE);
//                    Log.d(TAG, "handle immersive message");
                    if (mIsInImmersive) {
                        exitImmersive();
                    } else {
                        enterImmersive();
                    }
                }
            }
        };

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    Log.d(TAG, "send immersive message");
                    mWebViewClickHandler.removeMessages(CLICK_DELAY_MESSAGE);
                    mWebViewClickHandler.sendEmptyMessageDelayed(CLICK_DELAY_MESSAGE, CLICK_TIMEOUT);
                }

                return false;
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
        mLoadArticleListener = new APIRequestFinishListener<ArticleObject>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "load article failed, error: " + error.toString() + ", mIsFavArticle: " + mIsFavArticle);
                Toast.makeText(ArticleActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                setProgressBarVisibility(false);
            }

            @Override
            public void onGetResult(ArticleObject result) {
                setProgressBarVisibility(false);
                mArticle = result;
                mIsFavArticle = mArticle.isFav;
                Log.d(TAG, "finish load article , isFav: " + mArticle.isFav);
                initFavMenuItem();
                loadArticleHtml();
            }
        };

        mFavListener = new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                mIsInFavProcess = false;
                mIsFavArticle = !mIsFavArticle;
                setFavResult();
                Log.d(TAG, "fav set success");

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "fav set fail, error: " + error.toString() + ", articleId: " + mArticleId);
                if (mIsFavArticle) {
                    Toast.makeText(ArticleActivity.this, R.string.unfav_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ArticleActivity.this, R.string.fav_fail, Toast.LENGTH_SHORT).show();
                }
                mIsInFavProcess = false; 
                initFavMenuItem();

            }

            @Override
            public void onGetResult(Object result) {

            }
        };
    }

    private void setFavResult() {
        Intent intent = new Intent();
        intent.putExtra(IS_FAV_ARTICLE, mIsFavArticle);
        intent.putExtra(ARTICLE_ID, mArticleId);
        setResult(RESULT_OK, intent);
    }

    private void loadArticleObject() {
        if (mArticleId != -1) {
            setProgressBarVisibility(true);
            if (mIsFromFavArticleList) {
                mArticleService.loadFavArticle(mArticleId, mLoadArticleListener);
            } else {
                mArticleService.loadArticle(mArticleId, mLoadArticleListener);
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

    private void initFavMenuItem() {
        if (mFavItem != null) {
            if (mIsFavArticle) {
                mFavItem.setIcon(R.drawable.ic_rating_important);
            } else {
                mFavItem.setIcon(R.drawable.ic_rating_not_important);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
        mFavItem = menu.findItem(R.id.action_fav_article);
        //initFavMenuItem();
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
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_fav_article) {
            if (mIsInFavProcess) {
                Toast.makeText(this, R.string.wait_request_finsih, Toast.LENGTH_SHORT).show();
                return true;
            }
            mIsInFavProcess = true;
            if (mIsFavArticle) {
                if (mFavItem != null) {
                    mFavItem.setIcon(R.drawable.ic_rating_not_important);
                }
                mArticleService.unfavArticle(mArticleId, mFavListener);
            } else {
                if (mFavItem != null) {
                    mFavItem.setIcon(R.drawable.ic_rating_important);
                }
                mArticleService.favArticle(mArticleId, mFavListener);
            }
            return true;

        } else if (id == R.id.action_scroll_top) {
//            mWebView.scrollTo(0, 0);
            mWebView.smoothScrollToTop();
            return true;
        } else if (id == R.id.action_refresh) {
            loadArticleObject();
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