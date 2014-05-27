package cn.ragnarok.monoreader.app.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.test.api.TestUtil;
import cn.ragnarok.monoreader.app.ui.adapter.DrawerListAdapter;
import cn.ragnarok.monoreader.app.ui.fragment.TimelineFragment;

public class MainActivity extends Activity {

    public static final String TAG = "Mono.MainActivity";

    private TimelineFragment mTimelineFragment;

    private Fragment[] mFragmentList = new Fragment[4];

    private ListView mLeftDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TEST ONLY CODE
        APIService.getInstance().setHost(TestUtil.HOST);
        // END TEST ONLY CODE

//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);


        setContentView(R.layout.activity_main);

        mTimelineFragment = TimelineFragment.newInstance(false);
        mFragmentList[0] = mTimelineFragment;

        initDrawer();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, mTimelineFragment).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, mTimelineFragment).commit();
        }
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        mLeftDrawer = (ListView) findViewById(R.id.drawer);
        initDrawerList();


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            private int tempNagiviationMode;
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                tempNagiviationMode = getActionBar().getNavigationMode();
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setNavigationMode(tempNagiviationMode);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawerList() {
        mLeftDrawer.setAdapter(new DrawerListAdapter(this));
        mLeftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position < 1 && position > 0) {
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragmentList[position - 1]).commit();
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

}
