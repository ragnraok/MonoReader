package cn.ragnarok.monoreader.app.ui.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
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
import cn.ragnarok.monoreader.app.cache.BitmapDiskCache;
import cn.ragnarok.monoreader.app.cache.BitmapMemeoryCache;

/**
 * Created by ragnarok on 14-5-25.
 */
public class TimelineListAdapter extends BaseAdapter {

    public static final String TAG = "Mono.TimelineListAdapter";

    private static final int RATE = 8;

    private static final int MAX_TITLE_LENGTH = 40;

    private Context mContext;
    private boolean mIsFavTimeline;

    private int mLastShowPosition;
    private AnimationSet mItemShowAnimation;


    private int[] mDefaultColorArray = new int[]{R.color.timeline_item_color1, R.color.timeline_item_color2, R.color.timeline_item_color3,
            R.color.timeline_item_color4, R.color.timeline_item_color5};

    private ArrayList<ListArticleObject> mData;
    private ImageLoader mImageLoader;
    private BitmapMemeoryCache mImageMemoryCache;

    private boolean mIsFling = false;


    public TimelineListAdapter(Context context, boolean isFavTimeline) {
        this.mContext = context;
        this.mIsFavTimeline = isFavTimeline;
        mLastShowPosition = -1;

        mData = new ArrayList<ListArticleObject>();

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxSize = manager.getMemoryClass() / RATE;
        mImageMemoryCache = new BitmapMemeoryCache(mContext, 1024 * 1024 * maxSize);
        mImageLoader = new ImageLoader(APIService.getInstance().getQueue(), mImageMemoryCache);

        initShowAnimation();

    }

    public TimelineListAdapter(Context context, boolean isFavTimeline, Collection<ListArticleObject> initData) {
        this(context, isFavTimeline);
        mData.addAll(initData);
    }

    private void initShowAnimation() {
        mItemShowAnimation = new AnimationSet(false);
        Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        mItemShowAnimation.addAnimation(alphaAnimation);
        mItemShowAnimation.setFillAfter(true);

//        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//        translateAnimation.setDuration(500);
//        translateAnimation.setFillAfter(true);
//        mItemShowAnimation.addAnimation(translateAnimation);
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

    public void clearData() {
        this.mData.clear();
        this.notifyDataSetChanged();
    }

    public void setOnFling(boolean isFling) {
        this.mIsFling = isFling;
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
            holder.mIsShowAnim = false;
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        String title = article.title;
        if (title.length() > MAX_TITLE_LENGTH) {
            title = title.substring(0, MAX_TITLE_LENGTH) + "...";
        }
        holder.mTitleView.setText(title);
        holder.mSiteTitleView.setText(article.site);

//        if (i > mLastShowPosition && !holder.mIsShowAnim) {
//            view.startAnimation(mItemShowAnimation);
//            mLastShowPosition = i;
//            holder.mIsShowAnim = true;
//        }

        if (holder.mBackgroundImageView != null && article.coverUrl != null) {
            holder.mBackgroundImageView.setTag(article.coverUrl);
            loadItemCover(holder.mBackgroundImageView, article.coverUrl);
        }




        return view;
    }

    private void loadItemCover(final ImageView imageView, String url) {
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

                if (!mIsFling && imageContainer.getBitmap() != null) {
                    if (imageView.getTag().toString().equals(imageContainer.getRequestUrl())) {
                        Log.d(TAG, "set ImageView bitmap, url = " + imageView.getTag());
                        Bitmap bitmap = imageContainer.getBitmap();
                        imageView.setImageBitmap(bitmap);

                        // put to disk cache
                        if (!BitmapDiskCache.getInstance(mContext).exist(imageView.getTag().toString())) {
                            BitmapDiskCache.getInstance(mContext).put(imageView.getTag().toString(), bitmap);
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "load Bitmap error, " + volleyError.toString() + ", url = " + imageView.getTag());

                if (!mIsFling) {
                    boolean exist = BitmapDiskCache.getInstance(mContext).exist(imageView.getTag().toString());
                    if (exist) {
                        Bitmap bitmap = BitmapDiskCache.getInstance(mContext).get(imageView.getTag().toString());
                        imageView.setImageBitmap(bitmap);
                    }
                }

            }
        };
        mImageLoader.get(url, imageListener);
    }

    private class ViewHolder {
        ImageView mBackgroundImageView;
        TextView mTitleView;
        TextView mSiteTitleView;
        ListArticleObject article;
        boolean mIsShowAnim;
    }
}
