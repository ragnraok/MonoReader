package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;

import java.util.List;

import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-21.
 */
public class TimeLineService extends BaseAPIService {
    public static final String API_TAG = "TimeLine";
    private static final String DATA_KEY = "articles";

    /**
     * get the main timeline
     * @param page
     * @param resultListener
     * @param errorListener
     * may be throw PageSmallThanOneException
     */
    public void mainTimeline(int page, final APIResultListener<List<ListArticleObject>> resultListener,
                             final Response.ErrorListener errorListener) {
       timeline(false, page, resultListener, errorListener);
    }

    /**
     * get the fav timeline
     * @param page
     * @param resultListener
     * @param errorListener
     * may be throw PageSmallThanOneException
     */
    public void favTimeline(int page, final APIResultListener<List<ListArticleObject>> resultListener,
                            final Response.ErrorListener errorListener) {
        timeline(true, page, resultListener, errorListener);
    }

    private void timeline(boolean isFav, int page, final APIResultListener<List<ListArticleObject>> resultListener,
                          final Response.ErrorListener errorListener) {
        String url = null;
        if (isFav) {
            url = String.format(Constant.URL.FAV_TIMELINE, page);
            url = APIService.getInstance().createURL(url);
        }
        else {
            url = String.format(Constant.URL.MAIN_TIMELINE, page);
            url = APIService.getInstance().createURL(url);
        }
        BaseAPIGetRequest<List<ListArticleObject>> timelineRequest =
                new BaseAPIGetRequest<List<ListArticleObject>>(url, DATA_KEY, errorListener, resultListener);
        timelineRequest.get().setTag(API_TAG);
        if (APIService.getInstance().isInit()) {
            APIService.getInstance().queueJob(timelineRequest.get());
        }
    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}