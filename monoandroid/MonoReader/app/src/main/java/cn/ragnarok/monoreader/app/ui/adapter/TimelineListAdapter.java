package cn.ragnarok.monoreader.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ragnarok.monoreader.app.R;

/**
 * Created by ragnarok on 14-5-25.
 */
public class TimelineListAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsFavTimeline;

    public TimelineListAdapter(Context context, boolean isFavTimeline) {
        this.mContext = context;
        this.mIsFavTimeline = isFavTimeline;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.timeline_item, viewGroup, false);
            ViewHolder holder = new ViewHolder();
            holder.mBackgroundImageView = (ImageView) view.findViewById(R.id.item_background_image);
            holder.mTitleView = (TextView) view.findViewById(R.id.article_title);
            holder.mSiteTitleView = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);
        }
        return view;
    }

    private class ViewHolder {
        ImageView mBackgroundImageView;
        TextView mTitleView;
        TextView mSiteTitleView;
    }
}
