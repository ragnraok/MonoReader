package cn.ragnarok.monoreader.api.util;

/**
 * Created by ragnarok on 14-5-21.
 */
public class Constant {
    public static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int DATA_FORMAT_ERORR = 1;
        public static final int PAGE_SMALL_THAN_ONE = 2;
        public static final int SITE_NOT_EXIST = 3;
        public static final int ARTICLE_NOT_EXIST = 4;
    }
    public static class URL {
        public static final String API_PREFIX = "api/";
        public static final String MAIN_TIMELINE = API_PREFIX + "timeline/%d/";
        public static final String FAV_TIMELINE = API_PREFIX + "fav_site_timeline/%d/";


        public static final String LOAD_ARTICLE = API_PREFIX + "article/load/%d/";
        public static final String LOAD_FAV_ARTICLE = API_PREFIX + "article/load_fav/%d/";
        public static final String LOAD_ALL_FAV_ARTICLE_LIST = API_PREFIX + "article/fav_list/";
        public static final String LOAD_FAV_ARTICLE_LIST = API_PREFIX + "article/fav_list/%d/";
        public static final String FAV_ARTICLE = API_PREFIX + "article/fav/";
        public static final String UNFAV_ARTICLE = API_PREFIX + "article/unfav/";
    }
    public static class RequestTAG {
        public static final String TAG = "MonoReader";
    }

}
