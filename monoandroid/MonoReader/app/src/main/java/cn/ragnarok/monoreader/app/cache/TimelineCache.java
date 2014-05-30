package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

        readLastUpdateFromCache();

        getAllFavArticleListFromDisk();
        getAllFavTimelineFromDisk();
        getAllMainTimelineFromDisk();
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
        readDiskCacheInternal(mMainTimelineMemCache, mMainTimelineCacheDir);
    }

    private void getAllFavTimelineFromDisk() {
        readDiskCacheInternal(mFavTimelineMemCache, mFavTimelineCacheDir);
    }

    private void getAllFavArticleListFromDisk() {
        readDiskCacheInternal(mFavArticleListMemCache, mFavArticleListCacheDir);
    }

    private void readDiskCacheInternal(Map<Integer, ArrayList<ListArticleObject>> cacheData, File cacheDir) {
        cacheData.clear();
        File[] allCache = cacheDir.listFiles();
        for (File file : allCache) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader reader = new BufferedReader(isr);

                String line = "";
                StringBuilder content = new StringBuilder("");
                while (line != null) {
                    line = reader.readLine();
                    content.append(line);
                }

                reader.close();

                String decodeContent = new String(Base64.decode(content.toString().getBytes(), Base64.DEFAULT));
                Collection<TimelineCacheObject> cacheContent = new Gson().fromJson(decodeContent,
                        new TypeToken<Collection<TimelineCacheObject>>() {}.getType());
                if (cacheContent != null) {
                    for (TimelineCacheObject timelineCache : cacheContent) {
                        ArrayList<ListArticleObject> data = new ArrayList<ListArticleObject>();
                        data.addAll(Arrays.asList(timelineCache.articles));
                        cacheData.put(timelineCache.page, data);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<ListArticleObject> getMainTimeline(int page) {
        return mMainTimelineMemCache.get(page);
    }

    public Collection<ListArticleObject> getFavTimeline(int page) {
        return mFavTimelineMemCache.get(page);
    }

    public Collection<ListArticleObject> getFavArticleList(int page) {
        return mFavArticleListMemCache.get(page);
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

    private void flushCacheToDiskInternal(Map<Integer, ArrayList<ListArticleObject>> memCache, File dir) {
        String fileName = String.valueOf(System.currentTimeMillis());
        File cacheFile = new File(dir + File.separator + fileName);
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            cacheFile.createNewFile();
            fileWriter = new FileWriter(cacheFile);
            printWriter = new PrintWriter(fileWriter);


            ArrayList<TimelineCacheObject> cacheContent = new ArrayList<TimelineCacheObject>();
            for (int page : memCache.keySet()) {
                Collection<ListArticleObject> articles = memCache.get(page);
                TimelineCacheObject cacheObject = new TimelineCacheObject();
                cacheObject.page = page;
                cacheObject.articles = (ListArticleObject[]) articles.toArray();
                cacheContent.add(cacheObject);
            }
            String cacheStr = new Gson().toJson(cacheContent.toArray(), TimelineCacheObject[].class);
            printWriter.print(Base64.encodeToString(cacheStr.toString().getBytes(), Base64.DEFAULT));
            printWriter.flush();

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushMainTimelineCacheToDisk() {
        flushCacheToDiskInternal(mMainTimelineMemCache, mMainTimelineCacheDir);
    }

    public void flushFavTimelineCacheToDisk() {
        flushCacheToDiskInternal(mFavTimelineMemCache, mFavTimelineCacheDir);
    }

    public void flushFavArticleListCacheToDisk() {
        flushCacheToDiskInternal(mFavArticleListMemCache, mFavArticleListCacheDir);
    }
}
