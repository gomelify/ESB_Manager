package com.ronschka.david.esb;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    static String phpData = "Error!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhpClass phpClass = new PhpClass();
        phpClass.setContext(getApplicationContext());
        phpClass.execute();

        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new PrefsFragment()).commit();

        setPhpData();
    }

    public void setPhpData(){
        if(!(phpData=="Error!")) {
            SharedPreferences pref = getSharedPreferences("phpData", 0);
            phpData = pref.getString("PHP", "");
        }
        else{
            Log.d("ESBLOG", "BUGG2!!");
        }
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from the XML resource
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference listPreference = (ListPreference) findPreference("classList");
            setListPreferenceData(listPreference);
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
                classArray = teacherList.split(",");

                String roomList = phpArray[2].toString();
                roomArray = teacherList.split(",");

                CharSequence[] entries = teacherArray;
                CharSequence[] entryValues = teacherArray;
                lp.setEntries(entries);
                lp.setDefaultValue("1");
                lp.setEntryValues(entryValues);
            }
            else{
                Log.d("ESBLOG", "BUGG!!");
            }
        }
    }
}

