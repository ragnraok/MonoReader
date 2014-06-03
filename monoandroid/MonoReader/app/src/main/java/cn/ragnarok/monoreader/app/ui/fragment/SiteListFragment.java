package cn.ragnarok.monoreader.app.ui.fragment;



import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.SiteService;
import cn.ragnarok.monoreader.api.service.SubscribeService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.adapter.SiteListAdapter;
import cn.ragnarok.monoreader.app.util.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class SiteListFragment extends Fragment {

    public static String TAG = "Mono.SiteListFragment";

    private SubscribeService mSubscribeService;
    private SiteService mSiteService;

    private StickyListHeadersListView mSiteList;
    private ProgressBar mProgressBar;
    private PullToRefreshLayout mPtrLayout;

    private Collection<SiteObject> mSiteCollection;
    private APIRequestFinishListener<Collection<SiteObject>> mGetSiteListRequestListener;
    private SiteListAdapter mSiteListAdapter = null;

    private SubscribeFragment mSubscribeFragment;

    public static SiteListFragment newInstance() {
        SiteListFragment fragment = new SiteListFragment();
        return fragment;
    }
    public SiteListFragment() {
        // Required empty public constructor
        mSubscribeService = new SubscribeService();
        mSiteService = new SiteService();
        mSubscribeFragment = new SubscribeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Sites");
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        initGetSiteRequestListener();
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        mSiteList = (StickyListHeadersListView) view.findViewById(R.id.site_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress);
        mPtrLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

        initSiteList();
        initPtrLayout();

        mSubscribeFragment.setOnSubscribeSuccessListener(new SubscribeFragment.OnSubscribeSuccessListener() {
            @Override
            public void onSubscribeSuccess() {
                loadSiteList();
            }
        });

        return view;
    }

    private void initPtrLayout() {
        Options.Builder ptrOptions = Options.create();
        ptrOptions.refreshOnUp(true);
        ptrOptions.scrollDistance(0.4f);

        ActionBarPullToRefresh.from(getActivity()).theseChildrenArePullable(mSiteList.getWrappedList()).options(ptrOptions.build()).
                listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        loadSiteList();

                    }
                }).setup(mPtrLayout);
        loadSiteList();
    }

    private void initSiteList() {

        mSiteList.setDivider(getResources().getDrawable(R.drawable.list_divider));
        mSiteList.setDividerHeight((int) Utils.dpToPix(getActivity(), 0.5f));

        mSiteList.setDrawingListUnderStickyHeader(true);
        mSiteList.setAreHeadersSticky(true);
        mSiteList.setOnStickyHeaderOffsetChangedListener(new StickyListHeadersListView.OnStickyHeaderOffsetChangedListener() {
            @Override
            public void onStickyHeaderOffsetChanged(StickyListHeadersListView stickyListHeadersListView, View header, int offset) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
                }
            }
        });


    }

    private void loadSiteList() {
        mPtrLayout.setRefreshing(true);
        mSiteList.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSiteService.loadAllSite(mGetSiteListRequestListener);
    }

    private void initGetSiteRequestListener() {
        mGetSiteListRequestListener = new APIRequestFinishListener<Collection<SiteObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "get site list error: " + error.toString());
                Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                setLoadFinishViewVisibility();
                mPtrLayout.setRefreshComplete();
            }

            @Override
            public void onGetResult(Collection<SiteObject> result) {
                Log.d(TAG, "successfully get site list, size: " + result.size());
                setLoadFinishViewVisibility();
                mPtrLayout.setRefreshComplete();
                mSiteCollection = result;
                SiteObject[] data = new SiteObject[result.size()];
                mSiteCollection.toArray(data);
//                if (mSiteListAdapter == null) {
                    mSiteListAdapter = new SiteListAdapter(getActivity(), data);
                    mSiteList.setAdapter(mSiteListAdapter);
//                } else {
//                    mSiteListAdapter.setData(data);
//                }
            }
        };
    }

    private void setLoadFinishViewVisibility() {
        //if (mProgressBar.getVisibility() != View.GONE) {
            mProgressBar.setVisibility(View.GONE);
        //}
        //if (mSiteList.getVisibility() != View.VISIBLE) {
            mSiteList.setVisibility(View.VISIBLE);
        //}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private void subscribe() {
        mSubscribeFragment.show(getFragmentManager(), "Subscribe");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_subscribe) {
            subscribe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.site_list, menu);
    }
}
