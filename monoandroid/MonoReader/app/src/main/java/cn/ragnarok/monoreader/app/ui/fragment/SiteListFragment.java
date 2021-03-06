package cn.ragnarok.monoreader.app.ui.fragment;



import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.SiteService;
import cn.ragnarok.monoreader.api.service.SubscribeService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.activity.CategoryTimelineActivity;
import cn.ragnarok.monoreader.app.ui.activity.SiteArticleListActivity;
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
    private AbsListView.MultiChoiceModeListener mUnsubscribeMode;
    private ArrayList<Integer> mSelectSite = new ArrayList<Integer>();
    private ProgressDialog mProgressDialog;
    private SiteListAdapter.OnCategorySetFinishListener mCategorySetListener;

    public static SiteListFragment newInstance() {
        SiteListFragment fragment = new SiteListFragment();
        return fragment;
    }
    public SiteListFragment() {
        // Required empty public constructor
        mSubscribeService = new SubscribeService();
        mSiteService = new SiteService();
        mSubscribeFragment = new SubscribeFragment();
        mCategorySetListener = new SiteListAdapter.OnCategorySetFinishListener() {
            @Override
            public void onCategorySetFinish() {
                loadSiteList();
            }
        };
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

        mUnsubscribeMode = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                Log.d(TAG, "check: " + checked + ", positioin: " + position);
                int siteId = ((SiteObject)mSiteListAdapter.getItem(position)).siteId;
                if (checked) {
                    mSelectSite.add(siteId);
                } else {
                    mSelectSite.remove(new Integer(siteId));
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.site_actionmode, menu);
                mSelectSite.clear();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.action_unsubsribe) {
                    actionMode.finish();
                    mProgressDialog = ProgressDialog.show(getActivity(), "Please waiting", "");
                    unSubscribeSelectSite();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        };

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
        mSiteList.getWrappedList().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mSiteList.getWrappedList().setMultiChoiceModeListener(mUnsubscribeMode);

        mSiteList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                SiteObject site = (SiteObject) mSiteListAdapter.getItem(itemPosition);
                if (site != null) {
                    String category = site.category;
                    if (site.isUnClassified) {
                        category = getString(R.string.un_classified_name);
                    }
                    String[] categorySet = mSiteListAdapter.getCategorySet();
                    Log.d(TAG, "start category timelnie, category: " + category + ", categorySet: " + Arrays.toString(categorySet));
                    Intent intent = new Intent(getActivity(), CategoryTimelineActivity.class);
                    intent.putExtra(CategoryTimelineActivity.CATEGORY, category);
                    intent.putExtra(CategoryTimelineActivity.CATEGORY_SET, categorySet);

                    startActivity(intent);
                }

            }
        });

        mSiteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SiteArticleListActivity.class);
                if (mSiteListAdapter != null) {
                    SiteObject site = ((SiteObject)mSiteListAdapter.getItem(i));
                    intent.putExtra(SiteArticleListActivity.SITE_ID, site.siteId);
                    intent.putExtra(SiteArticleListActivity.SITE_TITLE, site.title);
                }
                startActivity(intent);

            }
        });


    }

    private void loadSiteList() {
        mPtrLayout.setRefreshing(true);
        mSiteList.setVisibility(View.GONE);
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
                    mSiteListAdapter.setOnCategorySetFinishListener(mCategorySetListener);
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

    private void unSubscribeSelectSite() {
        Log.d(TAG, "unsubscribe select site, size: " + mSelectSite.size());
        if (mSelectSite.size() > 0 && mSiteListAdapter != null) {
            mSubscribeService.bundleUnSubscribe(mSelectSite, new APIRequestFinishListener() {
                @Override
                public void onRequestSuccess() {

                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    loadSiteList();
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    Log.d(TAG, "bundle unsubscribe failed, error: " + error.toString());
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    loadSiteList();
                }

                @Override
                public void onGetResult(Object result) {

                }
            });
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSubscribeService.cancelRequest();
        mSiteService.cancelRequest();
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
