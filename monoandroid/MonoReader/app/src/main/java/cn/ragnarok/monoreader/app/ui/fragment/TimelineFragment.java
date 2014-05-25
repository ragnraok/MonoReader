package cn.ragnarok.monoreader.app.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.adapter.TimelineListAdapter;


public class TimelineFragment extends Fragment {

    public static final String TAG = "Mono.TimelineFragment";

    private static final String IS_FAV_TIMELINE = "is_fav_timeline";

    private boolean mIsFavTimeline = false;

    private ListView mTimelineList;
    private View mTimelineView;

    public static TimelineFragment newInstance(boolean isFavTimeline) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_FAV_TIMELINE, isFavTimeline);
        fragment.setArguments(args);
        return fragment;
    }


    public TimelineFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsFavTimeline = getArguments().getBoolean(IS_FAV_TIMELINE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mTimelineView = inflater.inflate(R.layout.fragment_timeline, container, false);
        mTimelineList = (ListView) mTimelineView.findViewById(R.id.timeline_list);

        initTimelineList();

        return mTimelineView;
    }

    private void initTimelineList() {
        mTimelineList.setAdapter(new TimelineListAdapter(getActivity(), false));
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
