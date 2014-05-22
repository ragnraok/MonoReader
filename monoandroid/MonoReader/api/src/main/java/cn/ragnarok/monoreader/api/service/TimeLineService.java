package cn.ragnarok.monoreader.api.service;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRawResultListener;
import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.base.APIResultListener;
import cn.ragnarok.monoreader.api.base.BaseAPIGetRequest;
import cn.ragnarok.monoreader.api.base.BaseAPIService;
import cn.ragnarok.monoreader.api.object.ListArticleObject;
import cn.ragnarok.monoreader.api.util.Constant;

/**
 * Created by ragnarok on 14-5-21.
 * TimeLineService, return DataType is List<ListArticleObject>
 */
public class TimeLineService extends BaseAPIService {
    public static final String API_TAG = "TimeLine";
    private static final String DATA_KEY = "articles";


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

        BaseAPIGetRequest timelineRequest = new BaseAPIGetRequest(url, DATA_KEY,
                requestFinishListener,
                new APIRawResultListener() {
            @Override
            public void handleRawJson(String rawJson) {
                Gson gson = new Gson();
                Type resultType = new TypeToken<Collection<ListArticleObject>>(){}.getType();
                Collection<ListArticleObject> result = gson.fromJson(rawJson, resultType);
                if (requestFinishListener != null) {
                    requestFinishListener.onGetResult(result);
                }
            }
        });
        timelineRequest.get().setTag(API_TAG);
        APIService.getInstance().queueJob(timelineRequest.get());
    }

    @Override
    public void cancelRequest() {
        APIService.getInstance().getQueue().cancelAll(API_TAG);
    }
}