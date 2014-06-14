package cn.ragnarok.monoreader.app.ui.fragment;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.PreferenceChangeEvent;

import cn.ragnarok.monoreader.api.service.APIService;
import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingFragment extends PreferenceFragment {

    public static final String TAG = "Mono.SettingFragment";

    private EditTextPreference mHostPreference;
    String mOriginHost;


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActivity().setTitle(R.string.setting);

        initPref();
    }

    private void initPref() {
        mHostPreference = (EditTextPreference) findPreference(Utils.HOST);
        mHostPreference.setKey(Utils.HOST);
        mOriginHost = getActivity().getPreferences(Context.MODE_PRIVATE).getString(Utils.HOST, "");
        mHostPreference.setSummary(mOriginHost);
        mHostPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                onUpdateHost(newValue.toString());
                return true;
            }
        });

    }

    private void onUpdateHost(String newValue) {
        if (!newValue.endsWith("/")) {
            newValue = newValue + "/";
        }
        if (newValue == null || newValue.length() == 0 || !Utils.checkIsURL(newValue)) {
            Toast.makeText(getActivity(), R.string.invalid_host_hint, Toast.LENGTH_SHORT).show();
            return;
        }

//        String oldHost = Uri.parse(mOriginHost).getHost();
//        String newHost = Uri.parse(newValue).getHost();
        if (!mOriginHost.equals(newValue)) {
            SharedPreferences hostPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = hostPref.edit();
            editor.putString(Utils.HOST, newValue);
            editor.commit();
            mHostPreference.setSummary(newValue.toString());
            APIService.getInstance().setHost(newValue.toString());
            Utils.clearDiskCache(getActivity());
        }
    }




}
