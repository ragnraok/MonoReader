package cn.ragnarok.monoreader.app.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by ragnarok on 14-5-28.
 */
public class ScrollableWebView extends WebView {

    public interface OnScrollChangeListener {
        void onScrollChange(int currHoriScroll, int currVertiScroll, int oldHoriScroll, int oldVertiScroll);
    }
    private OnScrollChangeListener mOnScrollChangeListener;

    public ScrollableWebView(Context context) {
        super(context);
    }

    public ScrollableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
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
}
