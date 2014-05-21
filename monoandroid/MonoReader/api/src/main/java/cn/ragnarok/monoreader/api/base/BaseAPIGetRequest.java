package cn.ragnarok.monoreader.api.base;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import cn.ragnarok.monoreader.api.util.ErrorHelper;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIGetRequest<DataType> {
    private String mDataKey;
    private Response.Listener<String> mResponseListener = null;
    private BaseAPIRequest mRequest;
    private String mUrl;
    private Response.ErrorListener mErrorListener;
    private APIResultListener<DataType> mResultListener;

    public BaseAPIGetRequest(String url, String dataKey, Response.ErrorListener errorListener, APIResultListener<DataType> resultListener) {
        this.mUrl = url;
        this.mDataKey = dataKey;
        this.mResultListener = resultListener;
        initResponseListener();
        initRequest();
    }

    private void initResponseListener() {
        mResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                Map<String, DataType> resultJson = gson.fromJson(s,
                        new TypeToken<Map<String, DataType>>(){}.getType());
                DataType result = resultJson.get(mDataKey);
                mResultListener.onResultGet(result);
            }
        };
    }

    private void initRequest() {
        mRequest = new BaseAPIRequest(Request.Method.GET, mUrl, mResponseListener, mErrorListener);
    }

    public BaseAPIRequest get() {
        return mRequest;
    }
}
