package com.ronschka.david.esb;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SubParserClass extends AsyncTask<String, Void, String>{

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String parsedText;

    public SubParserClass(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... data) {
        String url[] = data[0].split(" , "); //0 is sub current, 1 is sub next
        String user = data[1];
        String password = data[2];

        String auth = user + ":" + password;
        String encoded = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));

        try{
            //online use
            Document doc1 = Jsoup.connect(url[0])
                    .header("Authorization", "Basic " + encoded)
                    .timeout(5000)
                    .get();

            if(!url[1].equals(" ")){
                Document doc2 = Jsoup.connect(url[1])
                        .header("Authorization", "Basic " + encoded)
                        .timeout(5000)
                        .get();

                String nextWeek = doc2.text();
                nextWeek = nextWeek.replaceFirst("Montag","[ Montag ]");
                parsedText = doc1.text() + " SPLIT " + nextWeek;
            }
            else{
                parsedText = doc1.text();
            }

            //delete unnecessary parts
            parsedText = parsedText.replaceFirst("Montag","[ Montag ]");
            parsedText = parsedText.replaceAll(" Eduard-Spranger-Berufskolleg: Hamm DB~1~2016-2017~2", "");
            parsedText = parsedText.replaceAll("Tag Datum Klasse\\(n\\) Stunde Lehrer Raum Art Vertretungs-Text", "");
            parsedText = parsedText.replaceAll(" Mo ", " ~ "); //new splitter for cases -> ~
            parsedText = parsedText.replaceAll(" Di ", " ~ ");
            parsedText = parsedText.replaceAll(" Mi ", " ~ ");
            parsedText = parsedText.replaceAll(" Do ", " ~ ");
            parsedText = parsedText.replaceAll(" Fr ", " ~ ");

        }catch(Exception e){
            Log.d("ESBLOG", "Internet connection failed!");
            e.printStackTrace();
        }

        return parsedText;
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}