package cn.ragnarok.monreader.api.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ragnarok on 14-5-21.
 */
public class CategoryObject {
    @SerializedName("category_id")
    public int categoryId;
    public String name;
    @SerializedName("is_un_classified")
    public boolean isUnClassified;
}
