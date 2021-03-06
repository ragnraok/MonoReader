package cn.ragnarok.monoreader.api.object;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ragnarok on 14-5-20.
 */
public class ArticleObject {
    @SerializedName("article_id")
    public int articleId;
    public String title;
    public String site;
    public String updated;
    public String content;
    public String url;
    @SerializedName("cover_url")
    public String coverUrl;
    @SerializedName("is_fav")
    public boolean isFav = false;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
