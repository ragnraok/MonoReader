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
 * Created by ragnarok on 14-5-27.
 */
public class DrawerListAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mDrawerMenuItems;
    private int[] mIcons = new int[] {R.drawable.home, R.drawable.rss, /* R.drawable.categories_icon,*/ R.drawable.settings};

    public DrawerListAdapter(Context context) {
        mContext = context;
        mDrawerMenuItems = mContext.getResources().getStringArray(R.array.drawer_menu);
    }

    @Override
    public int getCount() {
        return mDrawerMenuItems.length + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0) {
            return i;
        }
        return mDrawerMenuItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (pos == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_list_header, viewGroup, false);
            TextView versionText = (TextView) view.findViewById(R.id.version_info);
            versionText.setText(String.format(mContext.getString(R.string.version_format), mContext.getString(R.string.version)));
            return view;
        }
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.drawer_item, viewGroup, false);
            ViewHolder holder = new ViewHolder();
            holder.mText = (TextView) view.findViewById(R.id.text);
            holder.mIcon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mText.setText(mDrawerMenuItems[pos - 1]);
        holder.mIcon.setImageResource(mIcons[pos - 1]);
        return view;
    }

    class ViewHolder {
        TextView mText;
        ImageView mIcon;
    }
}
