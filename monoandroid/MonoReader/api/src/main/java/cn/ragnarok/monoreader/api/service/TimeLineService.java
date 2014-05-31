package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.ChangeDateObject;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-21.
 * TimeLineService, return DataType is List<ListArticleObject>
 */
public class TimeLineService extends BaseAPIService {
    public static final String API_TAG = "TimeLine";
    private static final String DATA_KEY = "articles";
    private static final String CHECK_UPDATE_DATA_KEY = "change";

    /**
     *
     * @param page
     * @param requestFinishListener
     * may be throw PageSmallThanOneException
     */
    public void mainTimeline(int page, final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
       timeline(false, page, requestFinishListener);
    }

    /**
     *
     * @param page
     * @param requestFinishListener
     * may be throw PageSmallThanOneException
     */
    public void favTimeline(int page, final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        timeline(true, page, requestFinishListener);
    }

    private void timeline(boolean isFav, int page, final APIRequestFinishListener<Collection<ListArticleObject>> requestFinishListener) {
        String url = null;
        if (isFav) {
            url = String.format(Constant.URL.FAV_TIMELINE, page);
            url = APIService.getInstance().createURL(url);
        }
        else {
            url = String.format(Constant.URL.MAIN_TIMELINE, page);
            url = APIService.getInstance().createURL(url);
        }

        Type resultType = new TypeToken<Collection<ListArticleObject>>(){}.getType();
        BaseAPIGetRequest timelineRequest = new BaseAPIGetRequest(url, DATA_KEY, resultType, requestFinishListener);
        timelineRequest.get().setTag(API_TAG);
        APIService.getInstance().queueJob(timelineRequest.get());
    }

    public void mainTimelineUpdateCheck(APIRequestFinishListener<ChangeDateObject> requestFinishListener) {
        timelineCheckInternal(false, requestFinishListener);
    }

    public void favTimelineUpdateCheck(APIRequestFinishListener<ChangeDateObject> requestFinishListener) {
        timelineCheckInternal(true, requestFinishListener);
    }

    private void timelineCheckInternal(boolean isFav, APIRequestFinishListener<ChangeDateObject> requestFinishListener) {
        String url = null;
        if (isFav) {
            url = APIService.getInstance().createURL(Constant.URL.FAV_TIMELINE_CHECK_UPDATE);
        } else {
            url = APIService.getInstance().createURL(Constant.URL.MAIN_TIMELINE_CHECK_UPDATE);
        }

        Type resultType = new TypeToken<ChangeDateObject>(){}.getType();
        BaseAPIGetRequest request = new BaseAPIGetRequest(url, CHECK_UPDATE_DATA_KEY, resultType, requestFinishListener);
        request.get().setTag(API_TAG);
        APIService.getInstance().queueJob(request.get());
    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}