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
    //TODO need to rewrite
    public static final String TAG = "Mono.ArticleContentCache";

    public static final String ARTICLE_CACHE_DIR_NAME = "MonoArticleCache";

    private File mArticleCacheDir = null;

    private static ArticleContentCache mCache = null;

    private ArticleContentCache(Context context) {
        mArticleCacheDir = new File(context.getExternalFilesDir(ARTICLE_CACHE_DIR_NAME).getPath());

    }

    public static ArticleContentCache getInstance(Context context) {
        if (mCache == null) {
            mCache = new ArticleContentCache(context);
        }
        return mCache;
    }

    public void putArticle(ArticleObject article) {
        String filename = String.valueOf(article.articleId);
        File cacheFile = new File(mArticleCacheDir + File.separator + filename);

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

            Log.d(TAG, "put articleId: " + article.articleId + ", isFav: " + article.isFav);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArticleObject getArticle(int articleId) {
        File cacheFile = new File(mArticleCacheDir + File.separator + articleId);;
        Log.d(TAG, "getArticle, articleId: " + articleId);

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

    public void updateArticleFav(int articleId, boolean isFav) {
        File cacheFile = new File(mArticleCacheDir + File.separator + articleId);

        if (cacheFile.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
                BufferedReader reader = new BufferedReader(isr);
                String content = reader.readLine();
                reader.close();

                ArticleObject article = new Gson().fromJson(content, ArticleObject.class);

                article.isFav = isFav;

                putArticle(article);

                Log.d(TAG, "updateArticleFav, articleId: " + articleId + ", isFav: " + isFav);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
