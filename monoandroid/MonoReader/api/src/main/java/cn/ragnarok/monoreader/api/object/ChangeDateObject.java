package cn.ragnarok.monoreader.api.object;

import com.google.gson.Gson;

/**
 * Created by ragnarok on 14-5-31.
 */
public class ChangeDateObject {
    public long timestamp;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
