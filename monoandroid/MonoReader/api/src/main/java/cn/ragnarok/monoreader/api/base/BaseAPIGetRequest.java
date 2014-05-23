package cn.ragnarok.monoreader.api.base;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIGetRequest {
    public static final String TAG = "BaseAPIRequest";
    private String mDataKey;
    private Response.Listener<String> mResponseListener = null;
    private BaseAPIRequest mRequest;
    private String mUrl;
    private APIRequestFinishListener mRequesFinishListener;
    private Type mDataType;

    public BaseAPIGetRequest(String url, String dataKey, Type dataType, APIRequestFinishListener requestFinishListener) {
        this.mUrl = url;
        this.mDataKey = dataKey;
        this.mRequesFinishListener = requestFinishListener;
        this.mDataType = dataType;
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
//                Type resultType = new TypeToken<DataType>(){}.getType();
//                DataType result = gson.fromJson(dataJson, resultType);
//                mResultListener.onResultGet(result);

                if (mRequesFinishListener != null) {
                    mRequesFinishListener.onRequestSuccess();
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.isNull(mDataKey)) {
                        mRequesFinishListener.onGetResult(null);
                    } else {
                        Object resultJson = jsonObject.get(mDataKey);
                        String dataJson = resultJson.toString();
                        mRequesFinishListener.onGetResult(new Gson().fromJson(dataJson, mDataType));
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