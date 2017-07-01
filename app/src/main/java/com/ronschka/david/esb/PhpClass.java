package com.ronschka.david.esb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PhpClass extends AsyncTask<Void, Void, Void> {

    //Do this, if SettingsActivity is created
    private Context mContext;

    public void setContext(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Document list = Jsoup.connect("http://www.esb-hamm.de/app/kuerzel_infosystem.php")
                    .get();
            String classList = list.text();

            //set phpData from Settings to received php String
            SettingsActivity.phpData = classList;

            //Store the phpData in an Preference -> is used in SettingsActivity
            SharedPreferences classListStorage = mContext.getSharedPreferences("phpData", 0);
            SharedPreferences.Editor editor = classListStorage.edit();
            editor.putString("PHP", classList);
            editor.apply();

            Log.d("ESBLOG", classList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
