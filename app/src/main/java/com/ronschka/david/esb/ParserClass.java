package com.ronschka.david.esb;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserClass extends AsyncTask<String, Void, Void>{

    private String parsedText;

    @Override
    protected Void doInBackground(String... data) {
        String url = data[0];
        String user = data[1];
        String password = data[2];

        String auth = user + ":" + password;
        String encoded = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));

        try{
            Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encoded)
                    .get();
            parsedText = doc.text();

            //delete unnecessary parts
            parsedText = parsedText.replaceAll("Dienstag","");
            parsedText = parsedText.replaceAll("Mittwoch", "");
            parsedText = parsedText.replaceAll("Donnerstag", "");
            parsedText = parsedText.replaceAll("Freitag", "");
            parsedText = parsedText.replaceAll("Tag Datum Klasse\\(n\\) Stunde Lehrer Raum Art Vertretungs-Text", "");
            parsedText = parsedText.replaceAll("\\|","");
            parsedText = parsedText.replaceAll("\\[","");
            parsedText = parsedText.replaceAll("]","");

            String[] parsedArray = parsedText.split("Montag");

            for(int i = 1; i < 6; i++){
                String x;
                x = parsedArray[i].toString();
                Log.d("ESBLOG", "arrayValue: " + x);
            }

        }catch(Exception e){e.printStackTrace();}

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
