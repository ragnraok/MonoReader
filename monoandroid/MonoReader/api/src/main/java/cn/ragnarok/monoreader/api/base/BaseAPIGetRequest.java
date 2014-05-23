package cn.ragnarok.monoreader.api.base;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.ErrorHelper;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIGetRequest {
    public static final String TAG = "BaseAPIRequest";
    private String mDataKey;
    private Response.Listener<String> mResponseListener = null;
    private BaseAPIRequest mRequest;
    private String mUrl;
    private APIRawResultListener mRawResultListener;
    private APIRequestFinishListener mRequesFinishListener;

    public BaseAPIGetRequest(String url, String dataKey, APIRequestFinishListener requestFinishListener,
                             APIRawResultListener rawResultListener) {
        this.mUrl = url;
        this.mDataKey = dataKey;
        this.mRawResultListener = rawResultListener;
        this.mRequesFinishListener = requestFinishListener;
        initResponseListener();
        initRequest();
    }

    private void initResponseListener() {
        mResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                Gson gson = new Gson();
//                Map<String, DataType> resultJson = gson.fromJson(s,
//                        new TypeToken<Map<String, DataType>>(){}.getType());
//                String dataJson = resultJson.get(mDataKey).toString();
//                Log.d(TAG, dataJson);
//                Type resultType = new TypeToken<Collection<ListArticleObject>>(){}.getType();
//                Collection<ListArticleObject> result = gson.fromJson(dataJson, resultType);
//                mResultListener.onResultGet(result);

                if (mRequesFinishListener != null) {
                    mRequesFinishListener.onRequestSuccess();
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.isNull(mDataKey)) {
                        //mResultListener.onResultGet(null);
                        mRawResultListener.handleRawJson(null);
                    } else {
                        Object resultJson = jsonObject.get(mDataKey);
                        String dataJson = resultJson.toString();
                        mRawResultListener.handleRawJson(dataJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initRequest() {
        mRequest = new BaseAPIRequest(Request.Method.GET, mUrl, mResponseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mRequesFinishListener != null) {
                    mRequesFinishListener.onRequestFail(volleyError);
                }
            }
        });
    }

    public BaseAPIRequest get() {
        return mRequest;
    }
}
