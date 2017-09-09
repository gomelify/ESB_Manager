package com.ronschka.david.esb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    static String phpData = "";
    static String className = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setPhpData();

        //Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
    }
    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.setData(Uri.parse(className));
        setResult(RESULT_OK, data);
        finish();
        return;
    }
    //back arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

            //className will be returned to main, needed for toolbar
            if(listPreference.getEntry() != null){
                className = listPreference.getEntry().toString();
            }
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
                @Override
                //If the User changes the preferred school class..
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //..the new value will be saved!
                    listPreference.setValue(newValue.toString());
                    className = listPreference.getEntry().toString();
                    return false;
                }
            });
        }

        protected static void setListPreferenceData(ListPreference lp) {

            if(phpData != null && !phpData.isEmpty()){
                //split the String with full inforamtion by SPLIT
                CharSequence[] phpArray = phpData.split("SPLIT");

                //adding an array for every changeable element
                CharSequence[] teacherArray, classArray, roomArray;

                String teacherList = phpArray[0].toString();
                teacherArray = teacherList.split(",");

                String classList = phpArray[1].toString();
                classArray = classList.split(",");

                //ONLY FOR TEST
                classArray[classArray.length - 1] = "TEST";

                String roomList = phpArray[2].toString();
                roomArray = roomList.split(",");

                CharSequence[] entries = classArray;
                CharSequence[] entryValues = new CharSequence[classArray.length];

                for(int x = 0; x < classArray.length; x++){
                    entryValues[x] = Integer.toString(x+1);
                }

                lp.setEntries(entries);
                lp.setEntryValues(entryValues);
            }
            else{
                Log.d("ESBLOG", "No internet");
            }
        }
    }
}

