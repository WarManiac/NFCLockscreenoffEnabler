package pk.qwerty12.nfclockscreenoffenabler.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import pk.qwerty12.nfclockscreenoffenabler.Common;
import pk.qwerty12.nfclockscreenoffenabler.R;

/**
 * Fragment containing all of the settings possible for this app.
 **/
public class SettingsFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener ospcl;
    private boolean showInLauncher;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);
        showInLauncher = isShowInLauncher();

        ospcl = new SharedPreferences.OnSharedPreferenceChangeListener(){
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                refresh();
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.str_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(ospcl);
        refresh();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(ospcl);
        super.onPause();
    }

    private void refresh(){
        Intent i = new Intent(Common.SETTINGS_UPDATED_INTENT);
        Activity act = getActivity();
        act.sendBroadcast(i);

        if (isShowInLauncher() != showInLauncher){
            showInLauncher = isShowInLauncher();

            // Thanks to Chainfire for this
            // http://www.chainfire.eu/articles/133/_TUT_Supporting_multiple_icons_in_your_app/
            PackageManager pm = act.getPackageManager();
            int state = showInLauncher ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            ComponentName appPackage = new ComponentName(act, Common.MOD_PACKAGE_NAME + ".Activity-Launcher");
            pm.setComponentEnabledSetting(appPackage, state, PackageManager.DONT_KILL_APP);
        }

    }

    private boolean isShowInLauncher(){
        return getPreferenceManager().getSharedPreferences().getBoolean("show_in_launcher", true);
    }


}
