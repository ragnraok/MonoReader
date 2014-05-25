package cn.ragnarok.monoreader.api.base;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import cn.ragnarok.monoreader.api.util.Constant;
import cn.ragnarok.monoreader.api.util.ErrorHelper;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIRequest extends StringRequest {

    protected String mPostData = null;

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.setShouldCache(true);
    }

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String postData) {
        super(method, url, listener, errorListener);
        this.mPostData = postData;
        this.setShouldCache(true);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String json = null;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            json = new String(response.data);
        }
        try {
            JSONObject resultObject = new JSONObject(json);
            int errorCode = Integer.parseInt(resultObject.getString("error_code"));
            if (errorCode != Constant.ErrorCode.SUCCESS) {
                return ErrorHelper.handleError(errorCode); // return Response.error here, so that invoke errorListener
            } else {
                String data = resultObject.get("data").toString();
                return Response.success(data, makeCacheEntry(response));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.error(new ParseError());
    }

    private Cache.Entry makeCacheEntry(NetworkResponse response) {
        if (this.getMethod() == Method.POST) {
            return HttpHeaderParser.parseCacheHeaders(response); // not cache post request
        } else {
            /**
             * custom response cache, ignore "Cache-Control" header
             */
            long now = System.currentTimeMillis();
            Map<String, String> headers = response.headers;
            long serverDate = 0;
            if (headers.get("Date") != null) {
                serverDate = HttpHeaderParser.parseDateAsEpoch(headers.get("Date"));
            }

            final long cacheHitButRefreshed = 30 * 60 * 1000; // in 30 minutes cache will be hit, but also refreshed on background
            final long cacheExpired = 30 * 60 * 1000; // in 30 minutes this cache entry expires completely
            final long softExpire = now + cacheHitButRefreshed;
            final long ttl = now + cacheExpired;

            // the cache is available in 30 minutes
            Cache.Entry entry = new Cache.Entry();
            entry.data = response.data;
            entry.softTtl = softExpire;
            entry.ttl = ttl;
            entry.serverDate = serverDate;
            entry.responseHeaders = headers;

            return entry;
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (this.mPostData != null) {
            return this.mPostData.getBytes();
        }
        return super.getBody();
    }
}
