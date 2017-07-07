package com.ronschka.david.esb;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    static String phpData = "Error!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPhpData();

        //Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
    }

    public void setPhpData(){
            SharedPreferences pref = getSharedPreferences("phpData", 0);
            phpData = pref.getString("PHP", "");
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from the XML resource
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference listPreference = (ListPreference) findPreference("classList");
            setListPreferenceData(listPreference);

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
                @Override
                //If the User changes the preferred school class..
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //..the new value will be saved!
                    listPreference.setValue(newValue.toString());
                    return false;
                }
            });
        }

        protected static void setListPreferenceData(ListPreference lp) {

            if(!(phpData=="Error!")) {
                //split the String with full inforamtion by SPLIT
                CharSequence[] phpArray = phpData.split("SPLIT");

                //adding an array for every changeable element
                CharSequence[] teacherArray, classArray, roomArray;

                String teacherList = phpArray[0].toString();
                teacherArray = teacherList.split(",");

                String classList = phpArray[1].toString();
                classArray = classList.split(",");

                String roomList = phpArray[2].toString();
                roomArray = roomList.split(",");

                CharSequence[] entries = classArray;
                CharSequence[] entryValues = new CharSequence[classArray.length];

                for(int x = 0; x < classArray.length; x++){
                    entryValues[x] = Integer.toString(x+1);
                }

                lp.setEntries(entries);
                lp.setEntryValues(entryValues);

                Log.d("ESBLOG", "Entries" + lp.getValue());
            }
            else{
                Log.d("ESBLOG", "No internet");
            }
        }
    }
}

