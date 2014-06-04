package cn.ragnarok.monoreader.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.CategorySiteObject;
import cn.ragnarok.monoreader.api.object.SiteObject;
import cn.ragnarok.monoreader.api.service.SiteService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.ui.fragment.CategorySetFragment;
import cn.ragnarok.monoreader.app.ui.fragment.SiteListFragment;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ragnarok on 14-6-3.
 */
public class SiteListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    public static final String TAG = "Mono.SiteListAdapter";

    public interface OnCategorySetFinishListener {
        public void onCategorySetFinish();
    }

    private OnCategorySetFinishListener mOnCategorySetFinishListener;

    private Context mContext;
    private SiteObject[] mSiteList;
    private long[] mHeaderId;

    private View.OnClickListener mFavClickListener;
    private View.OnClickListener mCategeoryClickListener;
    private SiteService mSiteService;
    CategorySetFragment mCategorySetFragment = new CategorySetFragment();

    public SiteListAdapter(Context context, SiteObject[] siteList) {
        this.mContext = context;
        this.mSiteList = siteList;
        rearragneSiteList();

        mHeaderId = new long[siteList.length];
        for (int i = 0; i < mHeaderId.length; i++) {
            mHeaderId[i] = siteList[i].category.hashCode();
        }

        mSiteService = new SiteService();
        mCategorySetFragment.setCategorySetFinishListener(new CategorySetFragment.CategorySetFinishListener() {
            @Override
            public void onSetCategoryFinish() {
                if (mOnCategorySetFinishListener != null) {
                    mOnCategorySetFinishListener.onCategorySetFinish();
                }
            }
        });

        initClickListener();
    }

    private void rearragneSiteList() {
        Arrays.sort(mSiteList, new Comparator<SiteObject>() {
            @Override
            public int compare(SiteObject siteObject, SiteObject siteObject2) {
                return siteObject.category.compareTo(siteObject2.category);
//                if (siteObject.category.hashCode() > siteObject2.category.hashCode()) {
//                    return 1;
//                } else if (siteObject.category.hashCode() < siteObject2.category.hashCode()) {
//                    return -1;
//                } else {
//                    return 0;
//                }
            }
        });
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
                setSiteCategory(site);
            }
        };
    }

    private void setSiteCategory(SiteObject site) {
        mCategorySetFragment.setSiteId(site.siteId);
        mCategorySetFragment.show(((Activity)mContext).getFragmentManager(), "Category");
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
        long id =  mHeaderId[i];
//        Log.d(TAG, "headerId: " + id);
        return id;
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
            holder.favSetView.setOnClickListener(mFavClickListener);
            holder.catgorySetView.setOnClickListener(mCategeoryClickListener);
            view.setTag(holder);
        }
        SiteItemViewHolder holder = (SiteItemViewHolder) view.getTag();
        holder.siteTitle.setText(mSiteList[i].title);
        holder.siteUrl.setText(mSiteList[i].url);
        holder.siteLastUpdated.setText(mContext.getString(R.string.site_last_update_format, mSiteList[i].updated));
        holder.siteArticleNum.setText(mContext.getString(R.string.site_articles_num_format, mSiteList[i].articleCount));
        holder.favSetView.setTag(mSiteList[i]);
        holder.catgorySetView.setTag(mSiteList[i]);
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
