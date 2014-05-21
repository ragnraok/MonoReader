package cn.ragnarok.monoreader.api.base;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIRequest extends StringRequest {

    protected APIErrorListener mAPIErrorListener = null;
    protected String mPostData = null;

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                          APIErrorListener apiErrorListener) {
        super(method, url, listener, errorListener);
        this.mAPIErrorListener = apiErrorListener;
    }

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                          APIErrorListener apiErrorListener, String postData) {
        super(method, url, listener, errorListener);
        this.mAPIErrorListener = apiErrorListener;
        this.mPostData = postData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject resultObject = new JSONObject(json);
            int errorCode = Integer.parseInt(resultObject.getString("error_code"));
            if (errorCode != Constant.ErrorCode.SUCCESS && mAPIErrorListener != null) {
                mAPIErrorListener.handleError(errorCode);
            } else {
                String data = resultObject.getJSONObject("data").toString();
                return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.error(new ParseError());
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (this.mPostData != null) {
            return this.mPostData.getBytes();
        }
        return super.getBody();
    }
}
