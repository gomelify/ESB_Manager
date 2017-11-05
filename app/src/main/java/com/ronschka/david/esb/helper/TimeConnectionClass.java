package com.ronschka.david.esb.helper;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TimeConnectionClass extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String parsedText;

    public TimeConnectionClass(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... data) {
        String url = data[0];
        String user = data[1];
        String password = data[2];

        String auth = user + ":" + password;
        String encoded = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));

        try {
            //online use
            Document doc1 = Jsoup.connect(url)
                    .header("Authorization", "Basic " + encoded)
                    .timeout(5000)
                    .get();

            parsedText = parseTable(doc1);
        } catch (Exception e) {
            Log.d("ESBLOG", "Internet connection failed!" + e.getMessage());
            e.printStackTrace();
        }

        return parsedText;
    }
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    private String parseTable(Document doc){
        String subString = "";

        Elements tableElements = doc.select("table");

        Elements tableRowElements = tableElements.select("tr");

        for (int i = 7; i < tableRowElements.size(); i++) {
            Element row = tableRowElements.get(i);
            Elements rowItems = row.select("td");
            for (int j = 0; j < rowItems.size(); j++) {
                if(rowItems.get(j).hasAttr("colspan")){ //important because only parent td has all information
                    subString = subString + " SPLIT " + rowItems.get(j).text() + " ~ " + rowItems.get(j).attr("rowspan"); //rowspan /2 is the duration of the lesson
                }
                else if(rowItems.get(j).text().contains("8:00 8:45") ||
                        rowItems.get(j).text().contains("8:45 9:30") || rowItems.get(j).text().contains("9:50 10:35") ||
                        rowItems.get(j).text().contains("10:35 11:20") || rowItems.get(j).text().contains("11:35 12:20") ||
                        rowItems.get(j).text().contains("12:20 13:05") || rowItems.get(j).text().contains("13:20 14:05") ||
                        rowItems.get(j).text().contains("14:05 14:50") || rowItems.get(j).text().contains("15:05 15:50") ||
                        rowItems.get(j).text().contains("15:50 16:35") || rowItems.get(j).text().contains("17:00 17:45") ||
                        rowItems.get(j).text().contains("18:00 18:45") || rowItems.get(j).text().contains("18:45 19:30") ||
                        rowItems.get(j).text().contains("19:45 20:30") || rowItems.get(j).text().contains("20:30 21:15")){

                    subString = subString + " HOURSPLIT ";
                }
            }
        }
        return subString;
    }
}
