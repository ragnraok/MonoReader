package cn.ragnarok.monoreader.api.object;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Created by ragnarok on 14-5-23.
 */
public class CategorySiteObject {
    public String category;
    @SerializedName("is_un_classified")
    public boolean isUnClassified;

    public Collection<SiteObject> sites;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
