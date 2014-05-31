package cn.ragnarok.monoreader.app.cache;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import cn.ragnarok.monoreader.api.object.ArticleObject;

/**
 * Created by ragnarok on 14-5-30.
 */
public class ArticleContentCache {
    public static final String TAG = "Mono.ArticleContentCache";

    public static final String NORMAL_ARTICLE_CACHE_DIR_NAME = "MonoNormalArticleCache";
    public static final String FAV_ARTICLE_CACHE_DIR_NAME = "MonoFavArticleCache";

    private File mNormalArticleCacheDir = null;
    private File mFavArticleCacheDir = null;

    private static ArticleContentCache mCache = null;

    private ArticleContentCache(Context context) {
        mNormalArticleCacheDir = new File(context.getExternalFilesDir(NORMAL_ARTICLE_CACHE_DIR_NAME).getPath());
        mFavArticleCacheDir = new File(context.getExternalFilesDir(FAV_ARTICLE_CACHE_DIR_NAME).getPath());
    }

    public static ArticleContentCache getInstance(Context context) {
        if (mCache == null) {
            mCache = new ArticleContentCache(context);
        }
        return mCache;
    }

    public void putArticle(ArticleObject article, boolean isInFavArticleList) {
        String filename = String.valueOf(article.articleId);
        File cacheFile = null;
        if (isInFavArticleList) {
            cacheFile = new File(mFavArticleCacheDir + File.separator + filename);
        } else {
            cacheFile = new File(mNormalArticleCacheDir + File.separator + filename);
        }
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            cacheFile.createNewFile();
            fileWriter = new FileWriter(cacheFile);
            printWriter = new PrintWriter(fileWriter);

            String cacheContent = article.toString();
            printWriter.println(cacheContent);

            printWriter.flush();

            fileWriter.close();

            Log.d(TAG, "put articleId: " + article.articleId + ", isFav: " + article.isFav + ", isInFavArticleList: " + isInFavArticleList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArticleObject getArticle(int articleId, boolean isInFavArticleList) {
        File cacheFile = null;
        Log.d(TAG, "getArticle, articleId: " + articleId + ", isInFavArticleList: " + isInFavArticleList);
        if (isInFavArticleList) {
            cacheFile = new File(mFavArticleCacheDir + File.separator + articleId);
        } else {
            cacheFile = new File(mNormalArticleCacheDir + File.separator + articleId);
        }
        if (cacheFile.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
                BufferedReader reader = new BufferedReader(isr);
                String content = reader.readLine();
                reader.close();

                ArticleObject article = new Gson().fromJson(content, ArticleObject.class);

                Log.d(TAG, "getArticle, article.isFav: " + article.isFav);

                return article;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "getArticle, article cache is not exist");
        return null;
    }

    public void updateArticleFav(int articleId, boolean isFav, boolean isInFavArticleList) {
        File cacheFile = null;
        if (isInFavArticleList) {
            cacheFile = new File(mFavArticleCacheDir + File.separator + articleId);
        } else {
            cacheFile = new File(mNormalArticleCacheDir + File.separator + articleId);
        }
        if (cacheFile.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
                BufferedReader reader = new BufferedReader(isr);
                String content = reader.readLine();
                reader.close();

                ArticleObject article = new Gson().fromJson(content, ArticleObject.class);

                article.isFav = isFav;

                if (isInFavArticleList && !isFav) {
                    cacheFile.delete();
                }

                putArticle(article, isInFavArticleList);

                Log.d(TAG, "updateArticleFav, articleId: " + articleId + ", isFav: " + isFav + ", isInFavArticleList: " + isInFavArticleList);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
