package com.ronschka.david.esb;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserClass extends AsyncTask<Void, Void, Void>{

    private String words;

    @Override
    protected Void doInBackground(Void... params) {

        Log.d("ESBLOG", "Start Parsing..");

        String user = "";
        String password = "";

        String auth = user + ":" + password;

        String encoded = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));

        try{
            Document doc = Jsoup.connect("http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/26/w00000.htm")
                    .header("Authorization", "Basic " + encoded)
                    .get();

            Log.d("ESBLOG", "Parsed");
            words = doc.text();
        }catch(Exception e){e.printStackTrace();}

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);

        if(words != null) {
            Log.d("ESBLOG", words);
        }
    }
}
