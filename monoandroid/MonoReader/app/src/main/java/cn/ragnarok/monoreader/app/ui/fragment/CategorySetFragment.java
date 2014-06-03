package cn.ragnarok.monoreader.app.ui.fragment;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ragnarok.monoreader.app.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CategorySetFragment extends Fragment {


    public CategorySetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_set, container, false);
    }


}
