package cn.ragnarok.monoreader.app.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ArticleObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.ArticleService;
import cn.ragnarok.monoreader.api.service.TimeLineService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.activity.ArticleActivity;
import cn.ragnarok.monoreader.app.ui.activity.MainActivity;
import cn.ragnarok.monoreader.app.ui.adapter.TimelineListAdapter;
import cn.ragnarok.monoreader.app.util.Utils;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TimelineFragment extends Fragment {

    public static final String TAG = "Mono.TimelineFragment";

    private static final String IS_FAV_TIMELINE = "is_fav_timeline";
    private static final int PRE_LOAD_ITEM_OFFSET = 2;

    private boolean mIsFavTimeline = false;

    private ListView mTimelineList;
    private ProgressBar mLoadingProgress;
    private View mTimelineView;
    private TimeLineService mTimelineService;
    private ArticleService mArticleService;
    private APIRequestFinishListener<Collection<ListArticleObject>> mRequestFinishListener;
    private TimelineListAdapter mTimelineAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    private int mPage;
    private boolean mIsLastPage;
    private boolean mIsLoadingMore;

    private boolean mIsInFavArticle;

    public static TimelineFragment newInstance(boolean isFavTimeline) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_FAV_TIMELINE, isFavTimeline);
        fragment.setArguments(args);
        return fragment;
    }

    public TimelineFragment() {
        mTimelineService = new TimeLineService();
        mArticleService = new ArticleService();

        mPage = 1;
        mIsLastPage = false;
        mIsLoadingMore = false;
        mIsInFavArticle = false;

    }

    public void setIsFavTimeline(boolean isFav) {
        if (isFav != mIsFavTimeline || mIsInFavArticle) {
            mIsFavTimeline = isFav;
            resetTimeline();
        }
    }

    private void resetTimeline() {
        mPage = 1;
        mIsLastPage = false;
        mIsLoadingMore = false;
        mIsInFavArticle = false;
        mTimelineList.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        pullTimeline();
        mTimelineList.smoothScrollToPosition(0);
    }

    private void pullFavArticles() {
        mPage = 1;
        mIsLastPage = false;
        mIsLoadingMore = false;
        mIsInFavArticle = true;
        mIsFavTimeline = false;
        mTimelineList.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mTimelineAdapter.clearData();
        mPullToRefreshLayout.setRefreshing(true);
        mTimelineList.smoothScrollToPosition(0);
        mArticleService.loadFavArticleList(mPage, mRequestFinishListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsFavTimeline = getArguments().getBoolean(IS_FAV_TIMELINE);
        }
        if (mTimelineAdapter == null) {
            mTimelineAdapter = new TimelineListAdapter(getActivity(), mIsFavTimeline);
        }

        if (mRequestFinishListener == null) {
            mRequestFinishListener = new APIRequestFinishListener<Collection<ListArticleObject>>() {
                @Override
                public void onRequestSuccess() {
                    mPullToRefreshLayout.setRefreshComplete();
                    mTimelineAdapter.setLoadingMore(false);
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    Log.d(TAG, "request faliled, " + error.toString());
                    mPullToRefreshLayout.setRefreshComplete();
                    Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onGetResult(Collection<ListArticleObject> result) {
                    Log.d(TAG, "result.size=" + result.size());

                    if (result.size() == 0) {
                        mIsLastPage = true;
                    }
                    mTimelineAdapter.appendData(result);
                    mIsLoadingMore = false;
                    if (mTimelineList.getVisibility() == View.GONE) {
                        mTimelineList.setVisibility(View.VISIBLE);
                        mLoadingProgress.setVisibility(View.GONE);
                    }
                }
            };
        }

        initSpinner();
    }

    private void initSpinner() {
        String items[] = new String[] {getString(R.string.main_timeline), getString(R.string.fav_timeline), getString(R.string.fav_article)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout, items);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                if (itemPosition == 0) {
                    mArticleService.cancelRequest();
                    setIsFavTimeline(false);
                } else if (itemPosition == 1) {
                    mArticleService.cancelRequest();
                    setIsFavTimeline(true);
                } else if (itemPosition == 2) {
                    mTimelineService.cancelRequest();
                    pullFavArticles();
                }
                return true;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mTimelineView = inflater.inflate(R.layout.fragment_timeline, container, false);
        mTimelineList = (ListView) mTimelineView.findViewById(R.id.timeline_list);
        mLoadingProgress = (ProgressBar) mTimelineView.findViewById(R.id.loading_progress);
        mPullToRefreshLayout = (PullToRefreshLayout) mTimelineView.findViewById(R.id.ptr_layout);

        initPullToRefreshLayout();
        initTimelineList();

        return mTimelineView;
    }

    private void initTimelineList() {
        mTimelineList.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);

        mTimelineList.setAdapter(mTimelineAdapter);
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
                    // reach bottom
//                    Log.d(TAG, "loading more timeline");
//                    Toast.makeText(getActivity(), "Loading More", Toast.LENGTH_SHORT).show();
                    mIsLoadingMore = true;
                    if (mIsInFavArticle) {
                        loadMoreFavAritcle();
                    } else {
                        loadMoreTimeline();
                    }

                }
            }
        });

        AnimationSet set = new AnimationSet(true);

        final Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        set.addAnimation(alphaAnimation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        mTimelineList.setLayoutAnimation(controller);

        mTimelineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListArticleObject article = (ListArticleObject) mTimelineAdapter.getItem(i);
                Intent articleIntent = new Intent(getActivity(), ArticleActivity.class);
                articleIntent.putExtra(ArticleActivity.ARTICLE_ID, article.articleId);
                articleIntent.putExtra(ArticleActivity.IS_FAV_ARTICLE, mIsInFavArticle);
                //startActivity(articleIntent);
                startActivityForResult(articleIntent, ArticleActivity.FAV_SET);
            }
        });

        pullTimeline();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ArticleActivity.FAV_SET) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isFav = data.getBooleanExtra(ArticleActivity.IS_FAV_ARTICLE, false);
                int articleId = data.getIntExtra(ArticleActivity.ARTICLE_ID, 0);
//                if (mIsInFavArticle) {
//                    pullFavArticles();
//                } else {
//
//                }
                mTimelineAdapter.updateArticleFav(articleId, isFav, mIsInFavArticle);
            }
        }
    }

    private void pullTimeline() {
        if (!Utils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
        }
        mTimelineList.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mTimelineAdapter.clearData();
        mPullToRefreshLayout.setRefreshing(true);
        if (mIsFavTimeline) {
            mTimelineService.favTimeline(mPage, mRequestFinishListener);
        } else {
            mTimelineService.mainTimeline(mPage, mRequestFinishListener);
        }
    }

    private void loadMoreTimeline() {
        if (!Utils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
        } else {
            mTimelineAdapter.setLoadingMore(true);
        }
        mPage++;
        if (mIsFavTimeline) {
            mTimelineService.favTimeline(mPage, mRequestFinishListener);
        } else {
            mTimelineService.mainTimeline(mPage, mRequestFinishListener);
        }
    }

    private void loadMoreFavAritcle() {
        if (!Utils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
        } else {
            mTimelineAdapter.setLoadingMore(true);
        }
        mPage++;
        mArticleService.loadFavArticleList(mPage, mRequestFinishListener);
    }

    private void initPullToRefreshLayout() {
        Options.Builder ptrOptions = Options.create();
        ptrOptions.refreshOnUp(true);
        ptrOptions.scrollDistance(0.4f);

        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().options(ptrOptions.build()).
                listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                mPage = 1;
                mIsLastPage = false;
                if (mIsInFavArticle) {
                    pullFavArticles();
                } else {
                    pullTimeline();
                }

            }
        }).setup(mPullToRefreshLayout);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        mTimelineService.cancelRequest();
    }

}
