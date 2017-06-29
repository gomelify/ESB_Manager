package com.ronschka.david.esb;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhpClass phpClass = new PhpClass();
        phpClass.setContext(getApplicationContext());
        phpClass.execute();

        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new PrefsFragment()).commit();

    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from the XML resource
            addPreferencesFromResource(R.xml.preferences);

        }
    }
}

