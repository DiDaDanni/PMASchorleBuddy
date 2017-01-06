package com.example.lena.schorlebuddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import static com.example.lena.schorlebuddy.MainActivity.ok;
import static com.example.lena.schorlebuddy.MainFragment.FILENAME;
import static com.example.lena.schorlebuddy.MainFragment.myNameView;
import static com.example.lena.schorlebuddy.MainFragment.username;

/**
 * Creates ActionBar with toolbar from app_bar_main which includes content_main
 * content_main is a dummy layout to be replaced by preferenceFragments
 * the preference layout is located in the xml directory
 */

public class ProfilSettingsActivity  extends AppCompatActivity {
    static boolean first = false;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        /* Replace Content with Fragment */
        getFragmentManager().beginTransaction().replace(R.id.app_frame, new MyPreferenceFragment()).commit();

    }

    /* Create PreferenceFragment and fill with content of xml.profil_preferences */
    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.profil_preferences);

            // reads from shared profil_preferences on change of said preference
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        // updates displayed profil_preferences if necessary as summary
        public void onResume() {
            super.onResume();
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        Preference singlePref = preferenceGroup.getPreference(j);
                        updatePreference(singlePref, singlePref.getKey());
                    }
                } else {
                    updatePreference(preference, preference.getKey());
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key), key);
            //immer wenn sich die preferences ändern
            if (key.equals("gender"))
            {
                ListPreference listPreference = (ListPreference) findPreference(key);
                String currValue = listPreference.getValue();
                if (currValue.equals("1"))  //weiblich
                {
                    CalculateFunction.gender = 1;
                }
                else if (currValue.equals("2")) //männlich
                {
                    CalculateFunction.gender = 2;
                }
            }
            else if (key.equals("weight")){
                EditTextPreference textPreference = (EditTextPreference) findPreference(key);
                String value = textPreference.getText();
                //if (!value.isEmpty())
                    CalculateFunction.weight = Integer.parseInt(value);
            }
            else if (key.equals("username")){
                EditTextPreference textPreference = (EditTextPreference)findPreference(key);
                String name = textPreference.getText();
                username = name;
            }

        }

        private void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());


                return;
            }

            SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
            preference.setSummary(sharedPrefs.getString(key, "Default"));
        }
    }
}
