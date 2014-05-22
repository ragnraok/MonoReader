package cn.ragnarok.monoreader.app.test;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.service.TimeLineService;

/**
 * Created by ragnarok on 14-5-22.
 */
public class TimelineTest {

    public static final String TAG = "Test.TimelineTest";

    private static TimelineTest test = null;
    private TimeLineService mTimelineService = null;

    private TimelineTest() {
        mTimelineService = new TimeLineService();
    }

    public static TimelineTest getTest() {
        if (test == null) {
            test = new TimelineTest();
        }
        return test;
    }

    public void testTimeline(final TextView text, int page) {
       testMainTimelin(text, page);
    }

    public void testMainTimelin(final TextView text, int page) {
        mTimelineService.mainTimeline(page, new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> articleList) {
                text.setText("");
                text.append("size: " + articleList.size() + "\n");
                text.append(dumpArticleList(articleList));
            }
        });
    }

    public void testFavTimeline(final TextView text, int page) {
        mTimelineService.favTimeline(page, new APIRequestFinishListener<Collection<ListArticleObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
                text.setText(volleyError.toString());
            }

            @Override
            public void onGetResult(Collection<ListArticleObject> articleList) {
                text.setText("");
                text.append("size: " + articleList.size() + "\n");
                text.append(dumpArticleList(articleList));
            }
        });
    }

    private String dumpArticleList(Collection<ListArticleObject> articleList) {
        StringBuilder result = new StringBuilder("");
        for (ListArticleObject article : articleList) {
            result.append(article.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
