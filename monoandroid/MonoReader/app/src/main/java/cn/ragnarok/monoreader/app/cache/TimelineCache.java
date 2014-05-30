package cn.ragnarok.monoreader.app.cache;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.ragnarok.monoreader.api.object.ListArticleObject;

/**
 * Created by ragnarok on 14-5-30.
 */
public class TimelineCache {
    public static final String TAG = "Mono.TimelineDiskCache";

    public static final String MAIN_TIMELINE_CACHE_DIR_NAME = "MainTimelineCache";
    public static final String FAV_TIMELINE_CACHE_DIR_NAME = "FavTimelineCache";
    public static final String FAV_ARTICLE_LIST_CACHE_DIR_NAME = "FavArticleListCache";

    private File mMainTimelineCacheDir = null;
    private File mFavTimelineCacheDir = null;
    private File mFavArticleListCacheDir = null;

    private Map<Integer, ArrayList<ListArticleObject>> mMainTimelineMemCache;
    private Map<Integer, ArrayList<ListArticleObject>> mFavTimelineMemCache;
    private Map<Integer, ArrayList<ListArticleObject>> mFavArticleListMemCache;

    private int mMainTimelinePage = 0;
    private int mFavTimelinePage = 0;
    private int mFavArticleListPage = 0;

    private long mMainTimelineLastUpdate = -1;
    private long mFavTimelineLastUpdate = -1;
    private long mFavArticleListLastUpdate = -1;

    private Context mContext;

    private static TimelineCache mCache;

    private TimelineCache(Context context) {
        mContext = context;

        mMainTimelineCacheDir = new File(mContext.getExternalFilesDir(MAIN_TIMELINE_CACHE_DIR_NAME).getPath());
        mFavTimelineCacheDir = new File(mContext.getExternalFilesDir(FAV_TIMELINE_CACHE_DIR_NAME).getPath());
        mFavArticleListCacheDir = new File(mContext.getExternalFilesDir(FAV_ARTICLE_LIST_CACHE_DIR_NAME).getPath());


        mMainTimelineMemCache = new HashMap<Integer, ArrayList<ListArticleObject>>();
        mFavArticleListMemCache = new HashMap<Integer, ArrayList<ListArticleObject>>();
        mFavTimelineMemCache = new HashMap<Integer, ArrayList<ListArticleObject>>();


    }

    private void readLastUpdateFromCache() {
        String[] allCacheFileName = mMainTimelineCacheDir.list();
        long time = -1;
        for (String t : allCacheFileName) {
            long tt = Long.parseLong(t);
            if (tt > time) {
                time = tt;
            }
        }
        if (time != -1) {
            mMainTimelineLastUpdate = time;
        } else {
            mMainTimelineLastUpdate = 0;
        }

        allCacheFileName = mFavTimelineCacheDir.list();
        time = -1;
        for (String t : allCacheFileName) {
            long tt = Long.parseLong(t);
            if (tt > time) {
                time = tt;
            }
        }
        if (time != -1) {
            mFavTimelineLastUpdate = time;
        } else {
            mFavTimelineLastUpdate = 0;
        }

        allCacheFileName = mFavArticleListCacheDir.list();
        time = -1;
        for (String t : allCacheFileName) {
            long tt = Long.parseLong(t);
            if (tt > time) {
                time = tt;
            }
        }
        if (time != -1) {
            mFavArticleListLastUpdate = time;
        } else {
            mFavArticleListLastUpdate = 0;
        }
    }

    public static TimelineCache getInstance(Context context) {
        if (mCache == null) {
            mCache = new TimelineCache(context);
        }
        return mCache;
    }

    public void putMainTimelineCache(Collection<ListArticleObject> newData, int page) {
        synchronized (mMainTimelineMemCache) {
            ArrayList<ListArticleObject> data = new ArrayList<ListArticleObject>();
            data.addAll(newData);
            mMainTimelineMemCache.put(page, data);
            mMainTimelinePage = page;
        }
    }

    public void putFavTimelineCache(Collection<ListArticleObject> newData, int page) {
        synchronized (mFavTimelineMemCache) {
            ArrayList<ListArticleObject> data = new ArrayList<ListArticleObject>();
            data.addAll(newData);
            mFavTimelineMemCache.put(page, data);
            mFavTimelinePage = page;
        }
    }

    public void putFavArticleListCache(Collection<ListArticleObject> newData, int page) {
        synchronized (mFavArticleListMemCache) {
            ArrayList<ListArticleObject> data = new ArrayList<ListArticleObject>();
            data.addAll(newData);
            mFavArticleListMemCache.put(page, data);
            mFavArticleListPage = page;
        }
    }

    private void getAllMainTimelineFromDisk() {

    }

    private void getAllFavTimelineFromDisk() {

    }

    private void getAllFavArticleListFromDisk() {

    }

    public Collection<ListArticleObject> getMainTimeline(int page) {
        return null;
    }

    public Collection<ListArticleObject> getFavTimeline(int page) {
        return null;
    }

    public Collection<ListArticleObject> getFavArticleList(int page) {
        return null;
    }

    public long getMainTimelineLastUpdate() {
        return mMainTimelineLastUpdate;
    }

    public long getFavTimelineLastUpdate() {
        return mFavTimelineLastUpdate;
    }

    public long getFavArticleListLastUpdate() {
        return mFavArticleListLastUpdate;
    }

    public void flushMainTimelineCacheToDisk() {
        String fileName = String.valueOf(System.currentTimeMillis());
        File cacheFile = new File(mMainTimelineCacheDir + File.separator + fileName);
        FileOutputStream fos = null;
    }
}
