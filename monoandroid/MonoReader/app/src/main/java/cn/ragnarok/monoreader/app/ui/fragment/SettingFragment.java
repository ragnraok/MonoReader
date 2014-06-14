package cn.ragnarok.monoreader.app.ui.fragment;



import android.os.Bundle;
import android.app.Fragment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.prefs.PreferenceChangeEvent;

import cn.ragnarok.monoreader.app.R;
import cn.ragnarok.monoreader.app.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingFragment extends PreferenceFragment {

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
        mOriginHost = getPreferenceManager().getSharedPreferences().getString(Utils.HOST, "");
        mHostPreference.setSummary(mOriginHost);
        mHostPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mHostPreference.setSummary(newValue.toString());
                onUpdateHost(newValue.toString());
                return true;
            }
        });

    }

    private void onUpdateHost(String newValue) {

    }




}
