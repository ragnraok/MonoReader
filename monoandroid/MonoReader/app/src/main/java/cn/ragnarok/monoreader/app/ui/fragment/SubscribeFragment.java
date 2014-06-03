package cn.ragnarok.monoreader.app.ui.fragment;



import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import cn.ragnarok.monoreader.api.base.APIRequestFinishListener;
import cn.ragnarok.monoreader.api.service.SubscribeService;
import cn.ragnarok.monoreader.app.R;


public class SubscribeFragment extends DialogFragment {

    public interface OnSubscribeSuccessListener {
        public void onSubscribeSuccess();
    }

    private EditText mSubscribeUrl;
    private Button mSubscribeButton;
    private ProgressBar mProgress;
    private LinearLayout mButtonBarLayout;

    private SubscribeService mSubscribeService;
    private APIRequestFinishListener mSubscribeListener;

    private OnSubscribeSuccessListener mOnSubscribeSuccessListener;

    public SubscribeFragment() {
        // Required empty public constructor
        mSubscribeService = new SubscribeService();


    }

    private void initSubscribeListener() {
        mSubscribeListener = new APIRequestFinishListener() {
            @Override
            public void onRequestSuccess() {
                Toast.makeText(getActivity(), R.string.subscribe_success, Toast.LENGTH_SHORT).show();
                if (mOnSubscribeSuccessListener != null) {
                    mOnSubscribeSuccessListener.onSubscribeSuccess();
                }
                dismiss();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                Toast.makeText(getActivity(), R.string.subscribe_fail, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onGetResult(Object result) {

            }
        };
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        mSubscribeUrl = (EditText) view.findViewById(R.id.subscribe_url);
        mSubscribeButton = (Button) view.findViewById(R.id.subscribe_ok);
        mButtonBarLayout = (LinearLayout) view.findViewById(R.id.button_bar);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        initSubscribe();
        return view;
    }

    private void initSubscribe() {
        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mSubscribeUrl.getText().toString();
                if (url != null && url.trim().length() != 0) {
                    setSubscribeVisibility();
                    mSubscribeService.subscribe(null, url, null, mSubscribeListener);
                } else {
                    Toast.makeText(getActivity(), R.string.subscribe_hint, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setOnSubscribeSuccessListener(OnSubscribeSuccessListener listener) {
        mOnSubscribeSuccessListener = listener;
    }

    private void setSubscribeVisibility() {
        mButtonBarLayout.setVisibility(View.GONE);
        mSubscribeUrl.setVisibility(View.GONE);

        mProgress.setVisibility(View.VISIBLE);
    }

    private void setSubscribeFinishVisibility() {
        mButtonBarLayout.setVisibility(View.VISIBLE);
        mSubscribeUrl.setVisibility(View.VISIBLE);

        mProgress.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initSubscribeListener();
    }
}
