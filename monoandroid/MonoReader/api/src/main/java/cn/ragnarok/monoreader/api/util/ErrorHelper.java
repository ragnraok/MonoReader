package cn.ragnarok.monoreader.api.util;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.exception.ArticleNotExistException;
import cn.ragnarok.monoreader.api.exception.DataFormatErrorException;
import cn.ragnarok.monoreader.api.exception.PageSmallThanOneException;
import cn.ragnarok.monoreader.api.exception.SiteNotExistException;

/**
 * Created by ragnarok on 14-5-22.
 */
public class ErrorHelper {
    public static Response handleError(int errorCode) {
        switch (errorCode) {
            case Constant.ErrorCode.ARTICLE_NOT_EXIST:
                return Response.error(new ArticleNotExistException());
            case Constant.ErrorCode.DATA_FORMAT_ERORR:
               return Response.error(new DataFormatErrorException());
            case Constant.ErrorCode.PAGE_SMALL_THAN_ONE:
                return Response.error(new PageSmallThanOneException());
            case Constant.ErrorCode.SITE_NOT_EXIST:
                return Response.error(new SiteNotExistException());
            default:
                return Response.error(new ParseError());
        }
    }
}
