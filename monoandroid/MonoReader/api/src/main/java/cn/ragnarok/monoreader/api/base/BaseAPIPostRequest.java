package cn.ragnarok.monoreader.api.base;

import com.android.volley.Response;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIPostRequest extends BaseAPIRequest {
    public BaseAPIPostRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                              APIErrorListener apiErrorListener, String postData) {
        super(Method.POST, url, listener, errorListener, apiErrorListener, postData);
    }
}
