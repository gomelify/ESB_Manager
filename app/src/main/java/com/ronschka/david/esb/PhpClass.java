package com.ronschka.david.esb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PhpClass extends AsyncTask<Void, Void, Void> {

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
            String[] classArray = classList.split(",");
            SharedPreferences classListStorage = mContext.getSharedPreferences("classList", 0);
            SharedPreferences.Editor editor = classListStorage.edit();
            editor.putString(classList, "");
            editor.apply();

            String test = classListStorage.getString("List", "");
            Log.d("ESBLOG", test);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
