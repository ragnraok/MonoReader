package cn.ragnarok.monoreader.app.ui.fragment;



import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collection;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.object.CategoryObject;
import cn.ragnarok.monoreader.api.service.CategoryService;
import cn.ragnarok.monoreader.app.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CategorySetFragment extends DialogFragment {

    public static final String TAG = "Mono.CategorySetFragment";

    public interface CategorySetFinishListener {
        public void onSetCategoryFinish();
    }

    private CategorySetFinishListener mCategorySetFinishListener;

    private CategoryService mCategoryService;
    private APIRequestFinishListener<Collection<CategoryObject>> mGetCategoryListRequestListener;
    private APIRequestFinishListener mSetCategoryRequestListener;

    private AutoCompleteTextView mCategoryView;
    private ProgressBar mProgressBar;
    private Button mOKButton;
    private ArrayAdapter<String> adapter;

    private int mSiteId = -1;

    public CategorySetFragment() {
        mCategoryService = new CategoryService();
    }

    public void setSiteId(int siteId) {
        mSiteId = siteId;
    }

    public void setCategorySetFinishListener(CategorySetFinishListener categorySetFinishListener) {
        mCategorySetFinishListener = categorySetFinishListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_set, container, false);
        mCategoryView = (AutoCompleteTextView) view.findViewById(R.id.category_name);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mOKButton = (Button) view.findViewById(R.id.set_category_button);

        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCategory();
            }
        });

        loadAllCategory();

        return view;
    }

    private void initRequestListener() {
        mGetCategoryListRequestListener = new APIRequestFinishListener<Collection<CategoryObject>>() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "load category list fail, error: " + error.toString());
                setRequestFinishVis();
            }

            @Override
            public void onGetResult(Collection<CategoryObject> result) {
                Log.d(TAG, "successfully get category list, result.size: " + result.size());
                ArrayList<String> categoryList = new ArrayList<String>();
                for (CategoryObject category : result) {
                    if (!category.isUnClassified) {
                        categoryList.add(category.name);
                    }
                }
                String[] data = new String[categoryList.size()];
                categoryList.toArray(data);
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        data);
                mCategoryView.setAdapter(adapter);
                setRequestFinishVis();
            }
        };

        mSetCategoryRequestListener = new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
//                setRequestFinishVis();
                if (mCategorySetFinishListener != null) {
                    mCategorySetFinishListener.onSetCategoryFinish();
                }
                dismiss();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                Log.d(TAG, "set category failed, error: " + error.toString());
//                setRequestFinishVis();
                Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onGetResult(Object result) {

            }
        };
    }

    private void setRequestFinishVis() {
        mProgressBar.setVisibility(View.GONE);
        mCategoryView.setVisibility(View.VISIBLE);
        mOKButton.setVisibility(View.VISIBLE);
    }

    private void setRequestStartVis() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCategoryView.setVisibility(View.GONE);
        mOKButton.setVisibility(View.GONE);
    }

    private void loadAllCategory() {
        setRequestStartVis();
        mCategoryService.loadAllCategoryList(mGetCategoryListRequestListener);
    }

    private void setCategory() {
        String category = mCategoryView.getText().toString();
        if (category == null || category.length() == 0) {
            Toast.makeText(getActivity(), R.string.category_set_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSiteId != -1) {
            setRequestStartVis();
            mCategoryService.setSiteCategory(mSiteId, category, mSetCategoryRequestListener);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.set_category);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRequestListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCategoryService.cancelRequest();
    }
}
