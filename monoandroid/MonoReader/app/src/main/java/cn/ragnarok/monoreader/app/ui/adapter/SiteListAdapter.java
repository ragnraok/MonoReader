package cn.ragnarok.monoreader.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.CategorySiteObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.SiteService;
import cn.ragnarok.monoreader.app.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ragnarok on 14-6-3.
 */
public class SiteListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    public interface OnCategorySetFinishListener {
        public void onCategorySetFinish(SiteObject newSetSite);
    }

    private OnCategorySetFinishListener mOnCategorySetFinishListener;

    private Context mContext;
    private SiteObject[] mSiteList;

    private View.OnClickListener mFavClickListener;
    private View.OnClickListener mCategeoryClickListener;
    private SiteService mSiteService;

    public SiteListAdapter(Context context, SiteObject[] siteList) {
        this.mContext = context;
        this.mSiteList = siteList;
        mSiteService = new SiteService();

        initClickListener();
    }

    public void setOnCategorySetFinishListener(OnCategorySetFinishListener listener) {
        mOnCategorySetFinishListener = listener;
    }

    private void initClickListener() {
        mFavClickListener = new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                final SiteObject site = (SiteObject) view.getTag();
                if (site.isFav) {
                    ((ImageView)view).setImageResource(R.drawable.ic_rating_not_important);
                    mSiteService.unfavSite(site.siteId, new APIRequestFinishListener() {
                        @Override
                        public void onRequestSuccess() {
                            Toast.makeText(mContext, R.string.unfav_success, Toast.LENGTH_SHORT).show();
                            site.isFav = false;
                            view.setTag(site);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onRequestFail(VolleyError error) {
                            ((ImageView)view).setImageResource(R.drawable.ic_rating_important);
                            Toast.makeText(mContext, R.string.unfav_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onGetResult(Object result) {

                        }
                    });
                } else {
                    ((ImageView)view).setImageResource(R.drawable.ic_rating_important);
                    mSiteService.favSite(site.siteId, new APIRequestFinishListener() {
                        @Override
                        public void onRequestSuccess() {
                            Toast.makeText(mContext, R.string.fav_success, Toast.LENGTH_SHORT).show();
                            site.isFav = true;
                            view.setTag(site);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onRequestFail(VolleyError error) {
                            ((ImageView)view).setImageResource(R.drawable.ic_rating_not_important);
                            Toast.makeText(mContext, R.string.fav_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onGetResult(Object result) {

                        }
                    });
                }
            }
        };

        mCategeoryClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final SiteObject site = (SiteObject) view.getTag();

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
        if (!mSiteList[i].isUnClassified) {
            holder.category.setText(mSiteList[i].category);
        } else {
            holder.category.setText(R.string.un_classified_name);
        }
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
            holder.catgorySetView = (ImageView) view.findViewById(R.id.category_set);
            holder.siteTitle = (TextView) view.findViewById(R.id.site_title);
            holder.siteUrl = (TextView) view.findViewById(R.id.site_url);
            holder.siteLastUpdated = (TextView) view.findViewById(R.id.site_last_updated);
            holder.siteArticleNum = (TextView) view.findViewById(R.id.site_articles_num);
            holder.favSetView.setTag(mSiteList[i]);
            holder.catgorySetView.setTag(mSiteList[i]);
            holder.favSetView.setOnClickListener(mFavClickListener);
            holder.catgorySetView.setOnClickListener(mCategeoryClickListener);
            view.setTag(holder);
        }
        SiteItemViewHolder holder = (SiteItemViewHolder) view.getTag();
        holder.siteTitle.setText(mSiteList[i].title);
        holder.siteUrl.setText(mSiteList[i].url);
        holder.siteLastUpdated.setText(mContext.getString(R.string.site_last_update_format, mSiteList[i].updated));
        holder.siteArticleNum.setText(mContext.getString(R.string.site_articles_num_format, mSiteList[i].articleCount));
        SiteObject site = (SiteObject) holder.favSetView.getTag();
        if (site.isFav) {
            holder.favSetView.setImageResource(R.drawable.ic_rating_important);
        } else {
            holder.favSetView.setImageResource(R.drawable.ic_rating_not_important);
        }
        return view;
    }

    class HeaderViewHolder {
        TextView category;
    }

    class SiteItemViewHolder {
        ImageView favSetView;
        ImageView catgorySetView;
        TextView siteTitle;
        TextView siteUrl;
        TextView siteLastUpdated;
        TextView siteArticleNum;
    }
}
