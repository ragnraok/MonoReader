package cn.ragnarok.monoreader.app.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.test.api.TestUtil;
import cn.ragnarok.monoreader.app.ui.adapter.DrawerListAdapter;
import cn.ragnarok.monoreader.app.ui.fragment.SettingFragment;
import cn.ragnarok.monoreader.app.ui.fragment.SiteListFragment;
import cn.ragnarok.monoreader.app.ui.fragment.TimelineFragment;
import cn.ragnarok.monoreader.app.util.Utils;

public class MainActivity extends Activity {

    public static final String TAG = "Mono.MainActivity";

    private static final int FRAGMENT_NUM = 3;

    private TimelineFragment mTimelineFragment;
    private SiteListFragment mSiteListFragment;
    private SettingFragment mSettingFragment;

    private Fragment[] mFragmentList = new Fragment[FRAGMENT_NUM];
    private int mCurrentSelectFragmentId = 0;
    private boolean mIsChangeFragment = false;

    private ListView mLeftDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private long tabHolderClickTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_main);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ImageView homeIcon = (ImageView) findViewById(android.R.id.home);
//        homeIcon.setPadding(0, 0, (int) Utils.dpToPix(this, getResources().getDimension(R.dimen.home_icon_right_padding)), 0);
        setTitle("");

        mTimelineFragment = TimelineFragment.newInstance(false);
        mFragmentList[0] = mTimelineFragment;

        mSiteListFragment = SiteListFragment.newInstance();
        mFragmentList[1] = mSiteListFragment;

        mSettingFragment = SettingFragment.newInstance();
        mFragmentList[2] = mSettingFragment;

        initDrawer();

        String host = getPreferences(Context.MODE_PRIVATE).getString(Utils.HOST, null);
        Log.d(TAG, "start main activity, host: " + host);
        if (host != null && host.length() != 0) {
            APIService.getInstance().setHost(host);
            getFragmentManager().beginTransaction().add(R.id.container, mTimelineFragment).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, mTimelineFragment).commit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.enter_host_layout, null);
            final EditText input = (EditText) view.findViewById(R.id.host);
            builder.setView(view);
            builder.setTitle(R.string.host_setting_title);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = input.getText().toString();
                    if (!url.endsWith("/")) {
                        url = url + "/";
                    }
                    if (Utils.checkIsURL(url)) {
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(Utils.HOST, url);
                        editor.commit();
                        APIService.getInstance().setHost(url);
                        getFragmentManager().beginTransaction().add(R.id.container, mTimelineFragment).commit();
                        getFragmentManager().beginTransaction().replace(R.id.container, mTimelineFragment).commit();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        //if (savedInstanceState == null) {

        //}


    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        mLeftDrawer = (ListView) findViewById(R.id.drawer);
        initDrawerList();


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            private int tempNagiviationMode;
            private String originTitle;
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                tempNagiviationMode = getActionBar().getNavigationMode();
                originTitle = getTitle().toString();
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                setTitle(R.string.app_name);
                mIsChangeFragment = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!mIsChangeFragment) {
                    getActionBar().setNavigationMode(tempNagiviationMode);
                    setTitle(originTitle);
                }


            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);



    }

    private void initDrawerList() {
        mLeftDrawer.setAdapter(new DrawerListAdapter(this));
        mLeftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position > 0) {
                    int selectFragmentId = position - 1;
                    Fragment selectFragment = mFragmentList[selectFragmentId];
                    if (selectFragmentId != mCurrentSelectFragmentId) {
                        mIsChangeFragment = true;
                    } else {
                        mIsChangeFragment = false;
                    }
                    mCurrentSelectFragmentId = selectFragmentId;
                    getFragmentManager().beginTransaction().replace(R.id.container, selectFragment).commit();
                }
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);


    }

//    public void setActionBarTitle(String title) {
//        TextView text = (TextView) getActionBar().getCustomView().findViewById(R.id.title);
//        text.setText(title);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        APIService.getInstance().cancelAllRequest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionbarTitleDoubleClickListener(final Runnable job) {
        ActionBar actionbar = getActionBar();
        actionbar.setCustomView(R.layout.actionbar_double_click_holder);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.getCustomView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - tabHolderClickTS < 300) {
                    // on double click

                    job.run();
                }
                tabHolderClickTS = SystemClock.elapsedRealtime();
            }
        });
    }
}
