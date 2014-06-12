package cn.ragnarok.monoreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.SiteService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.adapter.TimelineListAdapter;
import cn.ragnarok.monoreader.app.ui.fragment.TimelineFragment;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class SiteArticleListActivity extends Activity {

    public static final String TAG = "Mono.SiteArticleListActivity";

    public static final String SITE_ID = "SITE_ID";
    public static final String SITE_TITLE = "SITE_TITLE";

    private static final int PRE_LOAD_ITEM_OFFSET = 2;

    private int mSiteId = -1;
    private String mSiteTitle = null;

    private SiteService mSiteService;

    private PullToRefreshLayout mPtrLayout;
    private ListView mArticleListView;
    private ProgressBar mProgressBar;

    private int mPage = 1;
    private TimelineListAdapter mArticleListAdapter;
    private APIRequestFinishListener<Collection<ListArticleObject>> mArticleListRequestListener;

    private boolean mIsLoadingMore;
    private boolean mIsLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_article_list);

        mSiteId = getIntent().getIntExtra(SITE_ID, -1);
        mSiteTitle = getIntent().getStringExtra(SITE_TITLE);

        if (mSiteTitle != null) {
            setTitle(mSiteTitle);
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mSiteService = new SiteService();
        mArticleListAdapter = new TimelineListAdapter(this, false);

        initView();
        initRequestListener();
        resetArticleList();

        resetArticleList();
    }

    private void initRequestListener() {
        mArticleListRequestListener = new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Toast.makeText(SiteArticleListActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "get article list faied: " + error.toString() + ", page: " + mPage);
                mArticleListAdapter.setLoadingMore(false);
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> result) {
                mPtrLayout.setRefreshComplete();
                mArticleListAdapter.appendData(result);
                mProgressBar.setVisibility(View.GONE);
                mArticleListView.setVisibility(View.VISIBLE);
                if (result.size() == 0) {
                    mIsLastPage = true;
                }
                mIsLoadingMore = false;
                mArticleListAdapter.setLoadingMore(false);
                Log.d(TAG, "successfully get article list, result.size: " + result.size() + ", page: " + mPage);
            }
        };
    }

    private void initView() {
        mPtrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        mArticleListView = (ListView) findViewById(R.id.article_list);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        mArticleListView.setAdapter(mArticleListAdapter);
        mArticleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (state == SCROLL_STATE_IDLE) {
                    mArticleListAdapter.setOnFling(false);
                    mArticleListAdapter.notifyDataSetChanged();
                } else {
                    mArticleListAdapter.setOnFling(true);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - PRE_LOAD_ITEM_OFFSET && totalItemCount > 0
                        && !mIsLoadingMore && !mIsLastPage) {
                    mIsLoadingMore = true;
                    loadMoreArticleList();

                }

            }
        });
        mArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListArticleObject article = (ListArticleObject) mArticleListAdapter.getItem(i);
                Intent articleIntent = new Intent(SiteArticleListActivity.this, ArticleActivity.class);
                articleIntent.putExtra(ArticleActivity.ARTICLE_ID, article.articleId);
                //startActivity(articleIntent);
                startActivityForResult(articleIntent, ArticleActivity.FAV_SET);
            }
        });

        initPtrLayout();
    }

    private void initPtrLayout() {
        Options.Builder ptrOptions = Options.create();
        ptrOptions.refreshOnUp(true);
        ptrOptions.scrollDistance(0.4f);

        ActionBarPullToRefresh.from(this).theseChildrenArePullable(R.id.article_list, R.id.loading_layout).options(ptrOptions.build()).
                listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        resetArticleList();
                    }
                }).setup(mPtrLayout);
    }


    private void resetArticleList() {
        mProgressBar.setVisibility(View.VISIBLE);
        mArticleListView.setVisibility(View.GONE);
        mArticleListAdapter.clearData();
        mPage = 1;
        mIsLastPage = false;
        mIsLoadingMore = false;
        mPtrLayout.setRefreshing(true);
        mSiteService.loadSiteArticleList(mSiteId, mPage, mArticleListRequestListener);
    }

    private void loadMoreArticleList() {
        mIsLoadingMore = true;
        mPage++;
        mArticleListAdapter.setLoadingMore(true);
        mSiteService.loadSiteArticleList(mSiteId, mPage, mArticleListRequestListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.site_article_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
