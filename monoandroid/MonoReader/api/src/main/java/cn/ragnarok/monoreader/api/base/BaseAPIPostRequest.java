package cn.ragnarok.monoreader.api.base;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by ragnarok on 14-5-23.
 */
public class BaseAPIPostRequest {
    public static final String TAG = "BaseAPIPostRequest";

    private BaseAPIRequest mRequest = null;
    private String mUrl = null;
    private String mPostData = null;
    private APIRequestFinishListener mRequestFinishListener;

    public BaseAPIPostRequest(String url, String postData, APIRequestFinishListener requestFinishListener) {
        this.mUrl = url;
        this.mPostData = postData;
        this.mRequestFinishListener = requestFinishListener;
        this.initRequest();
    }

    private void initRequest() {
        this.mRequest = new BaseAPIRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (mRequestFinishListener != null) {
                    mRequestFinishListener.onRequestSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mRequestFinishListener != null) {
                    mRequestFinishListener.onRequestFail(volleyError);
                }
            }
        }, mPostData);
    }

    public BaseAPIRequest get() {
        return this.mRequest;
    }
}
