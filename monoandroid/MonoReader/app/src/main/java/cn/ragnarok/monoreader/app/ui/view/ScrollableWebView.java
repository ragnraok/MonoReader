package cn.ragnarok.monoreader.app.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by ragnarok on 14-5-28.
 */
public class ScrollableWebView extends WebView {

    public static final String TAG = "Mono.ScrollableWebView";

    private static final int SMOOTH_SCROLL_MESSAGE_DEALY = 50;
    private static final int SCROLL_OFFSET = 500;

    public interface OnScrollChangeListener {
        void onScrollChange(int currHoriScroll, int currVertiScroll, int oldHoriScroll, int oldVertiScroll);
    }
    private OnScrollChangeListener mOnScrollChangeListener;

    private boolean mIsInSmoothScrolling = false;
    private Handler mScrollHandler;
    private Runnable mSmoothScrollRunnable;
    private int mScrollOffset;

    public ScrollableWebView(Context context) {
        super(context);
        init();
    }

    public ScrollableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        init();
    }

    private void init() {
        mScrollHandler = new Handler(Looper.getMainLooper());

        mSmoothScrollRunnable = new Runnable() {
            @Override
            public void run() {
                setScrollY(getScrollY() - SCROLL_OFFSET);
                if (getScrollY() <= 0) {
                    setScrollY(0);
                    mIsInSmoothScrolling = false;
                } else {
                    mScrollHandler.postDelayed(mSmoothScrollRunnable, SMOOTH_SCROLL_MESSAGE_DEALY);
                }
            }
        };
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChange(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    public void smoothScrollToTop() {
        if (mIsInSmoothScrolling) {
            return;
        }
        int scrollY = getScrollY();
        int scrollX = getScrollX();
        mIsInSmoothScrolling = true;
        mScrollHandler.postDelayed(mSmoothScrollRunnable, SMOOTH_SCROLL_MESSAGE_DEALY);
    }
}
