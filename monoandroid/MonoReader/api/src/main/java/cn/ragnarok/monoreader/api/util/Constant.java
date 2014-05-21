package cn.ragnarok.monoreader.api.util;

/**
 * Created by ragnarok on 14-5-21.
 */
public class Constant {
    public static class ErrorCode {
        public static int SUCCESS = 0;
        public static int DATA_FORMAT_ERORR = 1;
        public static int PAGE_SMALL_THAN_ONE = 2;
        public static int SITE_NOT_EXIST = 3;
        public static int ARTICLE_NOT_EXIST = 4;
    }
    public static class URL {
        public static String API_PREFIX = "api/";
        public static String MAIN_TIMELINE = API_PREFIX + "timeline/%d/";
        public static String FAV_TIMELINE = API_PREFIX + "fav_site_timeline/%d/";
    }
    public static class RequestTAG {
        public static String TAG = "MonoReader";
    }

}
