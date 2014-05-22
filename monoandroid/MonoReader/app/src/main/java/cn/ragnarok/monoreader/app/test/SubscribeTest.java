package cn.ragnarok.monoreader.app.test;

import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.service.SubscribeService;

/**
 * Created by ragnarok on 14-5-23.
 */
public class SubscribeTest {
    public static final String TAG = "Test.SubscribeTest";

    private SubscribeService service = null;
    private static SubscribeTest test;

    private SubscribeTest() {
        service = new SubscribeService();
    }

    public static SubscribeTest getTest() {
        if (test == null) {
            test = new SubscribeTest();
        }
        return test;
    }

    public void testSubscribe(final TextView text, String title, String url, String category) {
        text.setText("");
        service.subscribe(title, url, category, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.append("success\n");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.append(error.toString() + "\n");
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

    public void testUnsubscribe(final TextView text, int siteId) {
        service.unsubscribe(siteId, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.append("success\n");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.append(error.toString() + "\n");
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }
}
