package cn.ragnarok.monoreader.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ragnarok.monoreader.api.object.CategorySiteObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.app.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ragnarok on 14-6-3.
 */
public class SiteListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context mContext;
    private SiteObject[] mSiteList;

    private View.OnClickListener mFavClickListener;

    public SiteListAdapter(Context context, SiteObject[] siteList) {
        this.mContext = context;
        this.mSiteList = siteList;

        initClickListener();
    }

    public void initClickListener() {
        mFavClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SiteObject site = (SiteObject) view.getTag();
                Toast.makeText(mContext, site.title, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void setData(SiteObject[] siteList) {
        this.mSiteList = siteList;
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.site_list_stick_header, viewGroup, false);
            HeaderViewHolder holder = new HeaderViewHolder();
            holder.category = (TextView) view.findViewById(R.id.category);
            view.setTag(holder);
        }
        HeaderViewHolder holder = (HeaderViewHolder) view.getTag();
//        if (!mSiteList[i].isUnClassified) {
            holder.category.setText(mSiteList[i].category);
//        } else {
//            holder.category.setText(R.string.un_classified_name);
//        }
        return view;

    }

    @Override
    public long getHeaderId(int i) {
        return mSiteList[i].category.hashCode();
    }

    @Override
    public int getCount() {
        return mSiteList.length;
    }

    @Override
    public Object getItem(int i) {
        return mSiteList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.site_item_layout, viewGroup, false);
            SiteItemViewHolder holder = new SiteItemViewHolder();
            holder.favSetView = (ImageView) view.findViewById(R.id.fav_set);
            holder.siteTitle = (TextView) view.findViewById(R.id.site_title);
            holder.siteUrl = (TextView) view.findViewById(R.id.site_url);
            holder.siteLastUpdated = (TextView) view.findViewById(R.id.site_last_updated);
            holder.siteArticleNum = (TextView) view.findViewById(R.id.site_articles_num);
            holder.favSetView.setTag(mSiteList[i]);
            holder.favSetView.setOnClickListener(mFavClickListener);
            view.setTag(holder);
        }
        SiteItemViewHolder holder = (SiteItemViewHolder) view.getTag();
        holder.siteTitle.setText(mSiteList[i].title);
        holder.siteUrl.setText(mSiteList[i].url);
        holder.siteLastUpdated.setText(mContext.getString(R.string.site_last_update_format, mSiteList[i].updated));
        holder.siteArticleNum.setText(mContext.getString(R.string.site_articles_num_format, mSiteList[i].articleCount));
        return view;
    }

    class HeaderViewHolder {
        TextView category;
    }

    class SiteItemViewHolder {
        ImageView favSetView;
        TextView siteTitle;
        TextView siteUrl;
        TextView siteLastUpdated;
        TextView siteArticleNum;
    }
}
