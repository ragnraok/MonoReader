package cn.ragnarok.monoreader.api.object;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ragnarok on 14-5-20.
 */
public class ListArticleObject {
    @SerializedName("article_id")
    public int articleId;
    public String title;
    public String site;
    public String updated;
    @SerializedName("cover_url")
    public String coverUrl;
    @SerializedName("is_fav")
    public boolean isFav = false;

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ListArticleObject) {
            return this.articleId == ((ListArticleObject)o).articleId;
        } else {
            return false;
        }

    }
}
