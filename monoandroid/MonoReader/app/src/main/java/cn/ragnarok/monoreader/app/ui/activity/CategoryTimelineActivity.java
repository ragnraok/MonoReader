package cn.ragnarok.monoreader.app.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.CategoryService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.adapter.TimelineListAdapter;
import cn.ragnarok.monoreader.app.util.Utils;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class CategoryTimelineActivity extends Activity {
    public static final String TAG = "Mono.CategoryTimelineActivity";

    public static final String CATEGORY = "category";
    public static final String CATEGORY_SET = "category_set";

    private static final int PRE_LOAD_ITEM_OFFSET = 2;

    private String mCurrentCategory;
    private String[] mCategorySet;

    private CategoryService mCategoryService;

    private ListView mTimelineList;
    private ProgressBar mProgressBar;
    private PullToRefreshLayout mPtrLayout;

    private TimelineListAdapter mTimelineAdapter;
    private APIRequestFinishListener<Collection<ListArticleObject>> mTimelineRequestListener;
    private boolean mIsLoadingMore = false;
    private boolean mIsLastPage = false;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_timeline);

        mCategoryService = new CategoryService();

        mCurrentCategory = getIntent().getStringExtra(CATEGORY);
        mCategorySet = getIntent().getStringArrayExtra(CATEGORY_SET);

        setTitle("");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, mCategorySet);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                mCurrentCategory = adapter.getItem(itemPosition);
                resetTimeline();
                return true;
            }
        });

        int currentItemPos = getCurrentCategoryPos();
        if (currentItemPos != -1) {
            actionBar.setSelectedNavigationItem(currentItemPos);
        }


        initRequestListener();
        initView();
//        resetTimeline();
    }

    private int getCurrentCategoryPos() {
        for (int i = 0; i < mCategorySet.length; i++) {
            if (mCurrentCategory.equals(mCategorySet[i])) {
                return i;
            }
        }
        return -1;
    }

    private void initView() {
        mPtrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        mTimelineList = (ListView) findViewById(R.id.timeline_list);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        initPtrLayout();
        initTimelineList();
    }

    private void initPtrLayout() {
        Options.Builder ptrOptions = Options.create();
        ptrOptions.refreshOnUp(true);
        ptrOptions.scrollDistance(0.4f);

        ActionBarPullToRefresh.from(this).theseChildrenArePullable(R.id.timeline_list).options(ptrOptions.build()).
                listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        resetTimeline();
                    }
                }).setup(mPtrLayout);
    }

    private void initTimelineList() {
        mTimelineAdapter = new TimelineListAdapter(this, false);
        mTimelineList.setAdapter(mTimelineAdapter);
        mTimelineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListArticleObject article = (ListArticleObject) mTimelineAdapter.getItem(i);
                Intent articleIntent = new Intent(CategoryTimelineActivity.this, ArticleActivity.class);
                articleIntent.putExtra(ArticleActivity.ARTICLE_ID, article.articleId);
                startActivity(articleIntent);

            }
        });
        mTimelineList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (state == SCROLL_STATE_IDLE) {
                    mTimelineAdapter.setOnFling(false);
                    mTimelineAdapter.notifyDataSetChanged();
                } else {
                    mTimelineAdapter.setOnFling(true);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount - PRE_LOAD_ITEM_OFFSET && totalItemCount > 0
                        && !mIsLoadingMore && !mIsLastPage) {
                    mIsLoadingMore = true;
                    mTimelineAdapter.setLoadingMore(true);
                    loadMoreTimeline();

                }
            }
        });
    }

    private void initRequestListener() {
        mTimelineRequestListener = new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {
                mPtrLayout.setRefreshComplete();
                mTimelineAdapter.setLoadingMore(false);
            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "load category timeline faile: " + error.toString());
                Toast.makeText(CategoryTimelineActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> result) {
                Log.d(TAG, "successfully get category timeline, result.size: " + result.size());

                mTimelineList.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                if (result.size() == 0) {
                    mIsLastPage = true;
                }
                mIsLoadingMore = false;
                mTimelineAdapter.appendData(result);
            }
        };
    }

    private void resetTimeline() {
        Log.d(TAG, "reset timeline");
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            return;
        }
//        mTimelineList.setSelection(0);
//        mTimelineList.setScrollY(0);
        mTimelineList.smoothScrollToPosition(0);
        mIsLastPage = false;
        mIsLoadingMore = false;
        mPage = 1;
        mTimelineList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTimelineAdapter.clearData();
        mPtrLayout.setRefreshing(true);

        if (mCurrentCategory.equals(getString(R.string.un_classified_name))) {
            mCategoryService.unclassifiedCategoryTimeline(mPage, mTimelineRequestListener);
        } else {
            mCategoryService.categoryTimeline(mCurrentCategory, mPage, mTimelineRequestListener);
        }
    }

    private void loadMoreTimeline() {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            mTimelineAdapter.setLoadingMore(false);
            return;
        }
        mIsLoadingMore = true;
        mPage++;
        if (mCurrentCategory.equals(getString(R.string.un_classified_name))) {
            mCategoryService.unclassifiedCategoryTimeline(mPage, mTimelineRequestListener);
        } else {
            mCategoryService.categoryTimeline(mCurrentCategory, mPage, mTimelineRequestListener);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_timeline, menu);
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
