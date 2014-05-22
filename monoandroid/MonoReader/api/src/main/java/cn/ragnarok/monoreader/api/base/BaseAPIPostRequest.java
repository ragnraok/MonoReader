package cn.ragnarok.monoreader.api.base;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by ragnarok on 14-5-23.
 */
public class BaseAPIPostRequest {
    public static final String TAG = "BaseAPIPostRequest";

    private BaseAPIRequest mRequest = null;
    private String mUrl = null;
    private String mPostData = null;
    private Response.ErrorListener mErrorListener;

    public BaseAPIPostRequest(String url, String postData, Response.ErrorListener errorListener) {
        this.mUrl = url;
        this.mPostData = postData;
        this.mErrorListener = errorListener;
        this.initRequest();
    }

    private void initRequest() {
        this.mRequest = new BaseAPIRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, mErrorListener, mPostData);
    }

    public BaseAPIRequest get() {
        return this.mRequest;
    }
}
