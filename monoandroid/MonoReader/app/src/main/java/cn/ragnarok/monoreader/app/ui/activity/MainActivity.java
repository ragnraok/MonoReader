package cn.ragnarok.monoreader.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.test.api.TestUtil;
import cn.ragnarok.monoreader.app.ui.fragment.TimelineFragment;

public class MainActivity extends Activity {

    public static final String TAG = "Mono.MainActivity";

    private TimelineFragment mTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TEST ONLY CODE
        APIService.getInstance().setHost(TestUtil.HOST);
        // END TEST ONLY CODE

//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        mTimelineFragment = TimelineFragment.newInstance(false);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, mTimelineFragment).commit();
            getFragmentManager().beginTransaction().attach(mTimelineFragment).commit();
        }
    }
}
