package com.pinodex.loadcentral;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.pinodex.loadcentral.Util.Preferences;
import com.pinodex.loadcentral.Util.TelephonyInfo;

public class SettingsActivity extends ActionBarActivity {

    public static boolean doRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new PrefsFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (doRefresh) {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);

            doRefresh = false;
        }
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs);

            ListPreference preferredSim = (ListPreference) findPreference("preferred_sim");
            /*
            preferredSim.setSummary("Dual SIM configuration is not yet supported." +
                    "If your phone has dual SIM cards, the first SIM will be used to send SMS.");
            */

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(App.getContext());

            if (telephonyInfo.isDualSIM()) {
                preferredSim.setEnabled(true);
                preferredSim.setEntryValues(new String[]{"0", "1", "2"});
                preferredSim.setEntries(new String[]{
                        "Always Ask",
                        "Sim 1",
                        "Sim 2"
                });

                if (preferredSim.getValue() == null) {
                    preferredSim.setValue("0");
                }
            }

            ListPreference preferredAccessNumber = (ListPreference) findPreference("preferred_access_number");
            String[] accessNumbers = TextUtils.split(Preferences.getString("access_numbers"), ",");

            preferredAccessNumber.setEntries(accessNumbers);
            preferredAccessNumber.setEntryValues(accessNumbers);

            if (preferredAccessNumber.getValue() == null) {
                preferredAccessNumber.setValue(accessNumbers[0]);
            }

            Preference accessNumbersLink = findPreference("access_numbers");
            accessNumbersLink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(App.getContext(), AccessNumberListActivity.class);
                    startActivity(intent);

                    return true;
                }
            });

            Preference aboutScreenLink =  findPreference("about_screen");
            aboutScreenLink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(App.getContext(), AboutScreenActivity.class);
                    startActivity(intent);

                    return true;
                }
            });
        }

    }

}
