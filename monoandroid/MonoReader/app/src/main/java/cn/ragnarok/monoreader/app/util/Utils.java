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
}
