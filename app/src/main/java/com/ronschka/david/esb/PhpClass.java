package com.ronschka.david.esb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PhpClass extends AsyncTask<Void, Void, Void> {

    //Do this, if SettingsActivity is created
    private Context mContext;

    //important for preferences!
    public void setContext(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            URL url = new URL("http://www.esb-hamm.de/app/kuerzel_vertretungsplan.php");
            InputStream in = url.openStream();

            Document list = Jsoup.parse(in,"ISO-8859-1", "http://www.esb-hamm.de/app/");
            String classList = list.text();

            //edit the output
            classList = classList.replace('"',' ');
            classList = classList.replaceAll(" ","");

            //set phpData from Settings to received php String
            SettingsActivity.phpData = classList;

            //Store the phpData in an Preference -> is used in SettingsActivity
            SharedPreferences classListStorage = mContext.getSharedPreferences("phpData", 0);
            SharedPreferences.Editor editor = classListStorage.edit();
            editor.putString("PHP", classList);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
