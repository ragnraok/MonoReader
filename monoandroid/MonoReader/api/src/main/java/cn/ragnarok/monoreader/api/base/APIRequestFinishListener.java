package cn.ragnarok.monoreader.api.base;

import com.android.volley.VolleyError;

/**
 * Created by ragnarok on 14-5-23.
 */
public interface APIRequestFinishListener<T> {
    public void onRequestSuccess();
    public void onRequestFail(VolleyError error);
    public void onGetResult(T result);
}
