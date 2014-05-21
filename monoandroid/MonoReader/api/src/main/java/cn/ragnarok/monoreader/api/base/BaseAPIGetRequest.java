package cn.ragnarok.monoreader.api.base;

import com.android.volley.Response;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIGetRequest extends BaseAPIRequest {
    public BaseAPIGetRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                             APIErrorListener apiErrorListener) {
        super(Method.GET, url, listener, errorListener, apiErrorListener);
    }
}
