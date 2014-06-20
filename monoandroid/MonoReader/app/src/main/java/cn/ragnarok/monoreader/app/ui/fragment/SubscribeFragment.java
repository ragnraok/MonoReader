package cn.ragnarok.monoreader.app.ui.fragment;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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
import cn.ragnarok.monoreader.app.util.Utils;


public class SubscribeFragment extends DialogFragment {

    public static final String TAG = "Mono.SubscribeFragment";

    public interface OnSubscribeSuccessListener {
        public void onSubscribeSuccess();
    }

    private EditText mSubscribeUrl;
    private ProgressBar mProgress;

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
                Log.d(TAG, "subscribe failed, error:" + error.toString());
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
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
////        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.setTitle(R.string.subscribe);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.subscribe);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                String url = mSubscribeUrl.getText().toString();
//                if (url != null && url.trim().length() != 0) {
//                    setSubscribeVisibility();
//                    Utils.hideKeyboard(getActivity(), mSubscribeUrl);
//                    mSubscribeService.subscribe(null, url, null, mSubscribeListener);
//                } else {
//                    Toast.makeText(getActivity(), R.string.subscribe_hint, Toast.LENGTH_SHORT).show();
//                }
            }
        });


        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_subscribe, null, false);
        mSubscribeUrl = (EditText) view.findViewById(R.id.subscribe_url);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);



        builder.setView(view);

        final AlertDialog dialog =  builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String url = mSubscribeUrl.getText().toString();
                        if (url != null && url.trim().length() != 0) {
                            setSubscribeVisibility();
                            Utils.hideKeyboard(getActivity(), mSubscribeUrl);
                            mSubscribeService.subscribe(null, url, null, mSubscribeListener);
                        } else {
                            Toast.makeText(getActivity(), R.string.subscribe_hint, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSubscribeUrl.requestFocus();
        mSubscribeUrl.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showKeyboard(getActivity(), mSubscribeUrl);
            }
        }, 50);

        return dialog;
    }

    public void setOnSubscribeSuccessListener(OnSubscribeSuccessListener listener) {
        mOnSubscribeSuccessListener = listener;
    }

    private void setSubscribeVisibility() {
        mSubscribeUrl.setVisibility(View.GONE);

        mProgress.setVisibility(View.VISIBLE);
    }

    private void setSubscribeFinishVisibility() {
        mSubscribeUrl.setVisibility(View.VISIBLE);

        mProgress.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initSubscribeListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSubscribeService.cancelRequest();
//        Log.d(TAG, "onDetach");
    }


}
