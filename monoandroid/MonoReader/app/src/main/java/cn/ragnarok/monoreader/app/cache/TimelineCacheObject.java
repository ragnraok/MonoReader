package cn.ragnarok.monoreader.app.cache;

import com.google.gson.Gson;

import java.util.Collection;

import cn.ragnarok.monoreader.api.object.ListArticleObject;

/**
 * Created by ragnarok on 14-5-31.
 */
public class TimelineCacheObject {
    public int page;
    public ListArticleObject[] articles;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
