package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

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

    private ArrayList<ListArticleObject> mMainTimelineMemCache;
    private ArrayList<ListArticleObject> mFavTimelineMemCache;
    private ArrayList<ListArticleObject> mFavArticleListMemCache;

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


        mMainTimelineMemCache = new ArrayList<ListArticleObject>();
        mFavArticleListMemCache = new ArrayList<ListArticleObject>();
        mFavTimelineMemCache = new ArrayList<ListArticleObject>();


    }

    public void init() {
        readLastUpdateFromCache();

        getAllFavArticleListFromDisk();
        getAllFavTimelineFromDisk();
        getAllMainTimelineFromDisk();

        Log.d(TAG, String.format("init, mMainTimelineLastUpdate: %d, mFavTimelineLastUpdate: %d, mFavArticleListLastUpdate: %d",
                mMainTimelineLastUpdate, mFavTimelineLastUpdate, mFavArticleListLastUpdate));
        Log.d(TAG, String.format("init, mMainTimelinePage: %d, mFavTimelinePage: %d, mFavArticleListPage: %d",
                mMainTimelinePage, mFavTimelinePage, mFavArticleListPage));
    }

    private void readLastUpdateFromCache() {
        String[] allCacheFileName = mMainTimelineCacheDir.list();
        long time = -1;
        if (allCacheFileName != null) {
            for (String t : allCacheFileName) {
                long tt = Long.parseLong(t);
                if (tt > time) {
                    time = tt;
                }
            }
        }

        if (time != -1) {
            mMainTimelineLastUpdate = time;
        } else {
            mMainTimelineLastUpdate = 0;
        }

        allCacheFileName = mFavTimelineCacheDir.list();
        time = -1;
        if (allCacheFileName != null) {
            for (String t : allCacheFileName) {
                long tt = Long.parseLong(t);
                if (tt > time) {
                    time = tt;
                }
            }
        }
        if (time != -1) {
            mFavTimelineLastUpdate = time;
        } else {
            mFavTimelineLastUpdate = 0;
        }

        allCacheFileName = mFavArticleListCacheDir.list();
        time = -1;
        if (allCacheFileName != null) {
            for (String t : allCacheFileName) {
                long tt = Long.parseLong(t);
                if (tt > time) {
                    time = tt;
                }
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
            Log.d(TAG, "putMainTimelineCache, newData.size: " + newData.size() + ", page: " + page +
                    ", mMainTimelinePage: " + mMainTimelinePage);
            if (page == 1) {
                mMainTimelineMemCache.clear();
                mMainTimelineMemCache.addAll(newData);
                mMainTimelinePage = page;
            }
            if (page > mMainTimelinePage) {
                mMainTimelineMemCache.addAll(newData);
                mMainTimelinePage = page;
            }

        }
    }

    public void putFavTimelineCache(Collection<ListArticleObject> newData, int page) {
        synchronized (mFavTimelineMemCache) {
            Log.d(TAG, "putFavTimelineCache, newData.size: " + newData.size() + ", page: " + page +
                    ", mFavTimelinePage: " + mFavTimelinePage);
            if (page == 1) {
                mFavTimelineMemCache.clear();
                mFavTimelineMemCache.addAll(newData);
                mFavTimelinePage = page;
            }
            if (page > mFavTimelinePage) {
                mFavTimelineMemCache.addAll(newData);
                mFavTimelinePage = page;
            }

        }
    }

    public void putFavArticleListCache(Collection<ListArticleObject> newData, int page) {
        synchronized (mFavArticleListMemCache) {
            Log.d(TAG, "putFavArticleListCache, newData.size: " + newData.size() + ", page: " + page +
                    ", mFavArticleListPage: " + mFavArticleListPage);
            if (page == 1) {
                mFavArticleListMemCache.clear();
                mFavArticleListMemCache.addAll(newData);
                mFavArticleListPage = page;
            }
            if (page > mFavArticleListPage) {
                mFavArticleListMemCache.addAll(newData);
                mFavArticleListPage = page;
            }

        }
    }

    private void getAllMainTimelineFromDisk() {
        mMainTimelinePage = readDiskCacheInternal(mMainTimelineMemCache, mMainTimelineCacheDir, String.valueOf(mMainTimelineLastUpdate));
    }

    private void getAllFavTimelineFromDisk() {
        mFavTimelinePage = readDiskCacheInternal(mFavTimelineMemCache, mFavTimelineCacheDir, String.valueOf(mFavTimelineLastUpdate));
    }

    private void getAllFavArticleListFromDisk() {
        mFavArticleListPage = readDiskCacheInternal(mFavArticleListMemCache, mFavArticleListCacheDir, String.valueOf(mFavArticleListLastUpdate));
    }

    private int readDiskCacheInternal(ArrayList<ListArticleObject> cacheData, File cacheDir, String filename) {
        cacheData.clear();
        File cacheFile = new File(cacheDir + File.separator + filename);
        if (cacheFile.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
                BufferedReader reader = new BufferedReader(isr);
                String content = reader.readLine();

                reader.close();

                String decodeContent = content;
                TimelineCacheObject cacheContent = new Gson().fromJson(decodeContent,
                        new TypeToken<TimelineCacheObject>() {}.getType());
                Log.d(TAG, "readDiskCacheInternal, cacheContent.articles.length: " + cacheContent.articles.length +
                        ", cacheContent.lastpage: " + cacheContent.lastpage);
                if (cacheContent != null) {
                    cacheData.addAll(Arrays.asList(cacheContent.articles));
                    return cacheContent.lastpage;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "readDiskCacheInternal, filename: " + filename + " not exist");
        return 1;
    }

    public void clearMainTiemelineCache() {
        mMainTimelineMemCache.clear();
        File[] mainTimelineCacheFile = mMainTimelineCacheDir.listFiles();
        if (mainTimelineCacheFile != null) {
            for (File f : mainTimelineCacheFile) {
                f.delete();
            }
        }

        mMainTimelinePage = 1;
    }

    public void clearFavTimelineCache() {
        mFavTimelineMemCache.clear();
        File[] cacheFiles = mFavTimelineCacheDir.listFiles();
        if (cacheFiles != null) {
            for (File f : cacheFiles) {
                f.delete();
            }
        }

        mFavTimelinePage = 1;
    }

    public void clearFavArticleListCache() {
        mFavArticleListMemCache.clear();
        File[] cacheFiles = mFavArticleListCacheDir.listFiles();
        if (cacheFiles != null) {
            for (File f : cacheFiles) {
                f.delete();
            }
        }

        mFavArticleListPage = 1;
    }

    public ArrayList<ListArticleObject> getMainTimeline() {
        return mMainTimelineMemCache;
    }

    public ArrayList<ListArticleObject> getFavTimeline() {
        return mFavTimelineMemCache;
    }

    public ArrayList<ListArticleObject> getFavArticleList() {
        return mFavArticleListMemCache;
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

    public void setMainTimelineLastUpdate(long update) {
        mMainTimelineLastUpdate = update;
    }

    public void setFavTimelineLastUpdate(long update) {
        mFavTimelineLastUpdate = update;
    }

    public void setFavArticleListLastUpdate(long update) {
        mFavArticleListLastUpdate = update;
    }

    public int getMainTimelineLastPage() {
        return mMainTimelinePage;
    }

    public int getFavTimelineLastPage() {
        return mFavTimelinePage;
    }

    public int getFavArticleListLastPage() {
        return mFavArticleListPage;
    }

    private void flushCacheToDiskInternal(ArrayList<ListArticleObject> memCache, File dir, String fileName, int lastpage) {
        File cacheFile = new File(dir + File.separator + fileName);
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            cacheFile.createNewFile();
            fileWriter = new FileWriter(cacheFile);
            printWriter = new PrintWriter(fileWriter);


            TimelineCacheObject cacheContent = new TimelineCacheObject();
            ListArticleObject[] articles = new ListArticleObject[memCache.size()];
            memCache.toArray(articles);
            cacheContent.articles = articles;
            cacheContent.lastpage = lastpage;

            Log.d(TAG, "flushCacheToDiskInternal, articles.size: " + cacheContent.articles.length + ", lastpage: " + cacheContent.lastpage);

            String cacheStr = new Gson().toJson(cacheContent, TimelineCacheObject.class);
            printWriter.print(cacheStr.toString());
            printWriter.flush();

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushMainTimelineCacheToDisk() {
        flushCacheToDiskInternal(mMainTimelineMemCache, mMainTimelineCacheDir, String.valueOf(mMainTimelineLastUpdate), mMainTimelinePage);
    }

    public void flushFavTimelineCacheToDisk() {
        flushCacheToDiskInternal(mFavTimelineMemCache, mFavTimelineCacheDir, String.valueOf(mFavTimelineLastUpdate), mFavTimelinePage);
    }

    public void flushFavArticleListCacheToDisk() {
        flushCacheToDiskInternal(mFavArticleListMemCache, mFavArticleListCacheDir, String.valueOf(mFavArticleListLastUpdate), mFavArticleListPage);
    }

    public void flushCacheToDisk() {
        Log.d(TAG, "flushCacheToDisk");
        flushMainTimelineCacheToDisk();
        flushFavArticleListCacheToDisk();
        flushFavTimelineCacheToDisk();
    }

    public void clearCache() {
        mFavArticleListCacheDir.delete();
        mFavTimelineCacheDir.delete();
        mMainTimelineCacheDir.delete();
    }
}
