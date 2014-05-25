package cn.ragnarok.monoreader.app.ui.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;

import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.cache.BitmapCache;

/**
 * Created by ragnarok on 14-5-25.
 */
public class TimelineListAdapter extends BaseAdapter {

    public static final String TAG = "Mono.TimelineListAdapter";

    private static final int RATE = 8;

    private Context mContext;
    private boolean mIsFavTimeline;

    private int[] mDefaultColorArray = new int[]{R.color.timeline_item_color1, R.color.timeline_item_color2, R.color.timeline_item_color3,
            R.color.timeline_item_color4, R.color.timeline_item_color5};

    private ArrayList<ListArticleObject> mData;
    private ImageLoader mImageLoader;
    private BitmapCache mImageCache;

//    private static final int ITEM_TYPE_HAS_COVER = false;

    public TimelineListAdapter(Context context, boolean isFavTimeline) {
        this.mContext = context;
        this.mIsFavTimeline = isFavTimeline;

        mData = new ArrayList<ListArticleObject>();

        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        int maxSize = manager.getMemoryClass() / RATE;
        mImageCache = new BitmapCache(mContext, 1024 * 1024 * maxSize, false);
        mImageLoader = new ImageLoader(APIService.getInstance().getQueue(), mImageCache);
    }

    public TimelineListAdapter(Context context, boolean isFavTimeline, Collection<ListArticleObject> initData) {
        this(context, isFavTimeline);
        mData.addAll(initData);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void appendData(Collection<ListArticleObject> newData) {
        this.mData.addAll(newData);
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListArticleObject article = mData.get(i);
        if (view == null || ((ViewHolder) view.getTag()).article.equals(article) == false) {
            boolean isHasCover = article.coverUrl != null;
            if (isHasCover) {
                view = LayoutInflater.from(mContext).inflate(R.layout.timeline_item, viewGroup, false);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.timeline_item_without_cover, viewGroup, false);
            }
            ViewHolder holder = new ViewHolder();
            holder.article = article;
            if (isHasCover) {
                holder.mBackgroundImageView = (ImageView) view.findViewById(R.id.item_background_image);
                holder.mBackgroundImageView.setTag(article.coverUrl);
            } else {
                holder.mBackgroundImageView = null;
            }
            holder.mTitleView = (TextView) view.findViewById(R.id.article_title);
            holder.mSiteTitleView = (TextView) view.findViewById(R.id.site);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mTitleView.setText(article.title);
        holder.mSiteTitleView.setText(article.site);
        if (holder.mBackgroundImageView != null && article.coverUrl != null) {
            loadItemCover(holder.mBackgroundImageView, article.coverUrl);
        }

        return view;
    }

    private void loadItemCover(final ImageView imageView, String url) {
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

                if (imageContainer.getBitmap() != null) {
                    if (imageView.getTag().toString().equals(imageContainer.getRequestUrl())) {
                        imageView.setImageBitmap(imageContainer.getBitmap());
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        };
        mImageLoader.get(url, imageListener);
    }

    private class ViewHolder {
        ImageView mBackgroundImageView;
        TextView mTitleView;
        TextView mSiteTitleView;
        ListArticleObject article;
    }
}
