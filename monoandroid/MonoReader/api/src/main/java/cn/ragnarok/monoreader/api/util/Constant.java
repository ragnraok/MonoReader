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
        public static final String MAIN_TIMELINE_CHECK_UPDATE = API_PREFIX + "timeline/check_update/";
        public static final String FAV_TIMELINE_CHECK_UPDATE = API_PREFIX + "fav_site_timeline/check_update/";


        public static final String LOAD_ARTICLE = API_PREFIX + "article/load/%d/";
        public static final String LOAD_FAV_ARTICLE = API_PREFIX + "article/load_fav/%d/";
        public static final String LOAD_ALL_FAV_ARTICLE_LIST = API_PREFIX + "article/fav_list/";
        public static final String LOAD_FAV_ARTICLE_LIST = API_PREFIX + "article/fav_list/%d/";
        public static final String FAV_ARTICLE = API_PREFIX + "article/fav/";
        public static final String UNFAV_ARTICLE = API_PREFIX + "article/unfav/";
        public static final String FAV_ARTICLE_LIST_CHECK_UPDATE = API_PREFIX + "article/fav_list/check_update/";

        public static final String SUBSCRIBE = API_PREFIX + "subscribe/";
        public static final String UNSUBSCRIBE = API_PREFIX + "unsubscribe/";
        public static final String BUNDLE_UNSUBSCRIBE = API_PREFIX + "bundle_unsubscribe/";

        public static final String GET_ALL_CATEGORY = API_PREFIX + "category/get_all/";
        public static final String SET_CATEGORY = API_PREFIX + "category/set/";
        public static final String UNSET_CATEGORY = API_PREFIX + "category/unset/";
        public static final String CATEGORY_TIMELINE = API_PREFIX + "/category/%s/timeline/%d/";
        public static final String UNCLASSIFIED_CATEGORY_TIMELINE  = API_PREFIX + "/category/unclassified_timeline/%d/";

        public static final String LOAD_SITE_ALL_ARTICLE = API_PREFIX + "site/%d/articles/";
        public static final String LOAD_SITE_ARTICLE = API_PREFIX + "site/%d/articles/%d/";
        public static final String LOAD_ALL_SITE = API_PREFIX + "site/get_all/";
        public static final String LOAD_ALL_SITE_BY_CATEGORY = API_PREFIX + "site/get_all_by_category/";
        public static final String LOAD_ALL_SITE_IN_CATEGORY = API_PREFIX + "site/get_by_category/%s/";
        public static final String FAV_STIE = API_PREFIX + "site/fav_set/";
    }

}
