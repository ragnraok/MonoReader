package cn.ragnarok.monoreader.api.base;

/**
 * Created by ragnarok on 14-5-21.
 */
public interface APIResultListener<T> {
    public void onResultGet(T t);
}
