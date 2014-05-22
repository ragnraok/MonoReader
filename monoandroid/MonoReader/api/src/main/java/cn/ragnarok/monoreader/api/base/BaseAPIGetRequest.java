package cn.ragnarok.monoreader.api.base;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
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
    private Response.ErrorListener mErrorListener;
    //private APIResultListener<DataType> mResultListener;
    private APIRawResultListener mRawResultListener;

    public BaseAPIGetRequest(String url, String dataKey, Response.ErrorListener errorListener, APIRawResultListener rawResultListener) {
        this.mUrl = url;
        this.mDataKey = dataKey;
        //this.mResultListener = resultListener;
        this.mRawResultListener = rawResultListener;
        this.mErrorListener = errorListener;
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

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Object resultJson = jsonObject.get(mDataKey);
                    Log.d(TAG, resultJson.toString());
                    if (resultJson == null) {
                        //mResultListener.onResultGet(null);
                        mRawResultListener.handleRawJson(null);
                    } else {
//                        Gson gson = new Gson();
//                        String dataJson = resultJson.toString();
//                        Type dataType = new TypeToken<DataType>(){}.getType();
//                        DataType data = gson.fromJson(dataJson, dataType);
//                        mResultListener.onResultGet(data);
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
        mRequest = new BaseAPIRequest(Request.Method.GET, mUrl, mResponseListener, mErrorListener);
    }

    public BaseAPIRequest get() {
        return mRequest;
    }
}
