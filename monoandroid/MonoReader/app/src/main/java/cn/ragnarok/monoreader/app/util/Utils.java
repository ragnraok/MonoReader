package cn.ragnarok.monoreader.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import cn.ragnarok.monoreader.app.R;

/**
 * Created by ragnarok on 14-5-26.
 */
public class Utils {

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        return isMobileConnected(context) || isWifiConnected(context);

    }

    public static float dpToPix(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float pixToDp(Context context, float pix) {
        float scale = context.getResources().getDisplayMetrics().density;
        return pix * scale + 0.5f;
    }

    public static final String ASSET_DIR = "file:///android_asset/";

    // title, site-title, article-date, article-content
    public static final String ARTICLE_HTML_FORMAT = "<html>\n" +
            "  <head>\n" +
            "    <link rel=\"stylesheet\" href=\"css/bootstrap.min.css\" />\n" +
            "    <link rel=\"stylesheet\" href=\"css/style.css\" />\n" +
            "    <!--\n" +
            "    <meta name=\"viewport\" content=\"width=320, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" />\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, target-densityDpi=medium-dpi\"/>\n" +
            "    -->\n" +
            "    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;\"/>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"article-header\" class=\"page-header\">\n" +
            "      <div id=\"article-title\">%s</div>\n" +
            "      <div id=\"article-info\">\n" +
            "        <span id=\"site-title\">%s</span> <span id=\"site-date-gap\"> | </span> <span id=\"article-date\">%s</span>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    </div>\n" +
            "    <div id=\"article-content\">\n" +
            "       %s\n" +
            "    </div>\n" +
            "<!--\n" +
            "<div class=\"btn btn-default\" id=\"show-source-button\" >\n" +
            "  <a href=\"\">View Source</a>\n" +
            "</div>\n" +
            "-->\n" +
            "  </body>\n" +
            "</html>\n";
}
