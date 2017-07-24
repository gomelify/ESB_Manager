package com.ronschka.david.esb;

import android.os.AsyncTask;
import android.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserClass extends AsyncTask<String, Void, String>{

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String parsedText;

    public ParserClass(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... data) {
        String url = data[0];
        String user = data[1];
        String password = data[2];

        String auth = user + ":" + password;
        String encoded = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));

        try{
            //online use
            /*Document doc = Jsoup.connect(url)
                    .header("Authorization", "Basic " + encoded)
                    .timeout(10000)
                    .get(); */

            //local use
            Document doc = Jsoup.connect(url)
                    .timeout(10000)
                    .get();

            parsedText = doc.text();

            //delete unnecessary parts
            parsedText = parsedText.replaceFirst("Montag","[ Montag ]");
            parsedText = parsedText.replaceAll(" Eduard-Spranger-Berufskolleg: Hamm DB~1~2016-2017~2", "");
            parsedText = parsedText.replaceAll("Tag Datum Klasse\\(n\\) Stunde Lehrer Raum Art Vertretungs-Text", "");

        }catch(Exception e){e.printStackTrace();}

        return parsedText;
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}