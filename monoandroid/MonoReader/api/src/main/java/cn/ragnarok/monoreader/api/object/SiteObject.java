package cn.ragnarok.monoreader.api.object;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ragnarok on 14-5-21.
 */
public class SiteObject {
    @SerializedName("site_id")
    public int siteId;
    public String title;
    public String updated;
    public String category;
    @SerializedName("is_fav")
    public boolean isFav;
    @SerializedName("article_count")
    public int articleCount;
    public String url;
    @SerializedName("is_un_classified")
    public boolean isUnClassified;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}