package cn.ragnarok.monoreader.app.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.TimeLineService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.adapter.TimelineListAdapter;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TimelineFragment extends Fragment {

    public static final String TAG = "Mono.TimelineFragment";

    private static final String IS_FAV_TIMELINE = "is_fav_timeline";

    private boolean mIsFavTimeline = false;

    private ListView mTimelineList;
    private ProgressBar mLoadingProgress;
    private View mTimelineView;
    private TimeLineService mTimelineService;
    private APIRequestFinishListener<Collection<ListArticleObject>> mRequestFinishListener;
    private TimelineListAdapter mTimelineAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    public static TimelineFragment newInstance(boolean isFavTimeline) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_FAV_TIMELINE, isFavTimeline);
        fragment.setArguments(args);
        return fragment;
    }


    public TimelineFragment() {
        mTimelineService = new TimeLineService();

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
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    Log.d(TAG, "request faliled, " + error.toString());
                    mPullToRefreshLayout.setRefreshComplete();
                }

                @Override
                public void onGetResult(Collection<ListArticleObject> result) {
                    Log.d(TAG, "result.size=" + result.size());
                    mTimelineAdapter.appendData(result);
                    if (mTimelineList.getVisibility() == View.GONE) {
                        mTimelineList.setVisibility(View.VISIBLE);
                        mLoadingProgress.setVisibility(View.GONE);
                    }
                }
            };
        }
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
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });

        pullTimeline();

    }

    private void pullTimeline() {
        mTimelineAdapter.clearData();
        mPullToRefreshLayout.setRefreshing(true);
        if (mIsFavTimeline) {
            mTimelineService.favTimeline(1, mRequestFinishListener);
        } else {
            mTimelineService.mainTimeline(1, mRequestFinishListener);
        }
    }

    private void initPullToRefreshLayout() {
        Options.Builder ptrOptions = Options.create();
        ptrOptions.refreshOnUp(true);
        ptrOptions.scrollDistance(0.4f);

        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().options(ptrOptions.build()).
                listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                pullTimeline();
            }
        }).setup(mPullToRefreshLayout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("Timeline");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
