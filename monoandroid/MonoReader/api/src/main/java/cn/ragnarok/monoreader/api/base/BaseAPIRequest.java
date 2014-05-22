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
import cn.ragnarok.monoreader.api.util.ErrorHelper;

/**
 * Created by ragnarok on 14-5-21.
 */
public class BaseAPIRequest extends StringRequest {

    protected String mPostData = null;

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public BaseAPIRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String postData) {
        super(method, url, listener, errorListener);
        this.mPostData = postData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject resultObject = new JSONObject(json);
            int errorCode = Integer.parseInt(resultObject.getString("error_code"));
            if (errorCode != Constant.ErrorCode.SUCCESS) {
                return ErrorHelper.handleError(errorCode); // return Response.error here, so that invoke errorListener
            } else {
                String data = resultObject.get("data").toString();
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
