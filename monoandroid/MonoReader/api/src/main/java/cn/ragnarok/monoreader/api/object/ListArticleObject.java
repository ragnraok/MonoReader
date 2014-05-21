package cn.ragnarok.monoreader.api.object;

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

    public String toString() {
        return "ListArticle: " + articleId + " " + title + " " + site + " " + updated + " " + coverUrl;
    }
}
