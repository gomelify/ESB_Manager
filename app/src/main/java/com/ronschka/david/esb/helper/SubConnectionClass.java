package com.ronschka.david.esb.helper;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

public class SubConnectionClass extends AsyncTask<String, Void, String>{

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String parsedText;

    public SubConnectionClass(AsyncResponse delegate){
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
            Document doc1 = getDocument(url[0], encoded);

            String currentWeek = parseTable(doc1);
            String dateCurrentWeek = getDateOfTable(doc1);
            parsedText = currentWeek + " DATESPLIT " + dateCurrentWeek;

            Document doc2 = getDocument(url[1], encoded);

            if(doc2 != null){
                String nextWeek = parseTable(doc2);
                String dateNextWeek = getDateOfTable(doc2);

                parsedText = currentWeek + " DAYSPLIT " + nextWeek + " DATESPLIT "
                        + dateCurrentWeek + "," + dateNextWeek;
            }
        }
        catch(Exception e){
            Log.d("ESBLOG", "Internet connection failed! Sub");
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

        for(int x = 0; x < 5; x++) {
            Element table = doc.select("table").get(x); //select the table
            Elements rows = table.select("tr");

            if (rows.size() > 1) {
                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    if(i > 1){
                        subString = subString + " SPLIT " + cols.get(0).text() + "~" + cols.get(1).text() +
                                "~" + cols.get(2).text() + "~" + cols.get(3).text() +
                                "~" + cols.get(4).text() + "~" + cols.get(5).text() +
                                "~" + cols.get(6).text() + "~" + cols.get(7).text();
                    }
                    else{
                        subString = subString + cols.get(0).text() + "~" + cols.get(1).text() +
                                "~" + cols.get(2).text() + "~" + cols.get(3).text() +
                                "~" + cols.get(4).text() + "~" + cols.get(5).text() +
                                "~" + cols.get(6).text() + "~" + cols.get(7).text();
                    }
                }
            } else {
                Element row = rows.get(0);
                Elements cols = row.select("td");
                if(!subString.isEmpty()) {
                    subString = subString + cols.get(0).text();
                }
                else{
                    subString = cols.get(0).text();
                }
            }

            if(x != 4){
                subString = subString + " DAYSPLIT ";
            }
        }

        return subString;
    }

    public String getDateOfTable(Document doc){
        String parsedDoc = doc.text();
        String[] date = new String[5];

        //delete unnecessary parts
        parsedDoc = parsedDoc.replaceFirst("Montag","[ Montag ]");
        parsedDoc = parsedDoc.replaceAll("Tag Datum Klasse\\(n\\) Stunde Lehrer Raum Art Vertretungs-Text", "");
        parsedDoc = parsedDoc.replaceAll("\\|","");
        parsedDoc = parsedDoc.replaceAll(" \\[ Dienstag ] ","");
        parsedDoc = parsedDoc.replaceAll(" \\[ Mittwoch ] ","");
        parsedDoc = parsedDoc.replaceAll(" \\[ Donnerstag ] ","");
        ArrayList<String> parsedList = new ArrayList<>(
                Arrays.asList(parsedDoc.split("\\[ Montag ]"))); //split by [ Montag ] to prevent wrong splits

        for (int i = 0; i < 6; i++) {
            String x = parsedList.get(i);

            switch(i){
                case 0:
                    String[] y0 = x.split(" ");
                    date[0] = (y0[y0.length - 1]).trim();
                    break;
                case 1:
                    break;
                default:
                    String[] split = x.split("\\.");
                    date[i - 1] = (split[0] + "." + split[1] + ".").trim();
                    break;
            }
        }
        //separate every date with a comma to save it as a string
        StringBuilder dateBuilder = new StringBuilder();
        for (String n : date) {
            dateBuilder.append(n + ",");
        }
        dateBuilder.deleteCharAt(dateBuilder.length() - 1);

        return dateBuilder.toString();
    }

    private Document getDocument(String url, String header){
        try{
            Document data = Jsoup.connect(url)
                    .header("Authorization", "Basic " + header)
                    .timeout(5000)
                    .get();

            return data;
        }
        catch (Exception e){
            return null;
        }
    }
}