package cn.ragnarok.monoreader.app.test;

import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.Collection;
import java.util.List;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.CategoryObject;
import cn.ragnarok.monoreader.api.service.CategoryService;

/**
 * Created by ragnarok on 14-5-23.
 */
public class CategoryTest {
    public static final String TAG = "Test.CategoryTest";

    private static CategoryTest test = null;
    private CategoryService service = null;

    private CategoryTest() {
        service = new CategoryService();
    }

    public static CategoryTest getTest() {
        if (test == null) {
            test = new CategoryTest();
        }
        return test;
    }

    public void testGetAllCategory(final TextView text) {
        service.loadAllCategoryList(new APIRequestFinishListener<Collection<CategoryObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Collection<CategoryObject> result) {
                text.setText("");
                text.append(result.size() + "\n");
                text.append(dumpCategoryList(result));
            }
        });

    }

    public void testSetCategory(final TextView text, int siteId, String category) {
        service.setSiteCategory(siteId, category, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.setText("success");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

    public void testUnsetCategory(final TextView text, int siteId) {
        service.unsetSiteCategory(siteId, new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                text.setText("success");
            }

            @Override
            public void onRequestFail(VolleyError error) {
                text.setText(error.toString());
            }

            @Override
            public void onGetResult(Object result) {

            }
        });
    }

    private String dumpCategoryList(Collection<CategoryObject> categoryList) {
        StringBuilder result = new StringBuilder("");
        for (CategoryObject category : categoryList) {
            result.append(category.toString());
            result.append("\n");
        }
        return result.toString();
    }

}
