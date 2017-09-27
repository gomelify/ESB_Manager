package com.ronschka.david.esb.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.ronschka.david.esb.MainActivity;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.helper.RecyclerAdapter;
import com.ronschka.david.esb.helper.SubParserClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class SubstitutionClass{

    private boolean substitutionRefresh;
    private boolean firstStart;
    final private Context context;
    final private MainActivity mainAct;
    final private RecyclerView recyclerView;

    public SubstitutionClass(Context context, MainActivity mainAct, RecyclerView recyclerView){
        substitutionRefresh = true;
        firstStart = true;
        this.context = context;
        this.mainAct = mainAct;
        this.recyclerView = recyclerView;
    }

    public void parseSubstitution(){
        //parsedList is saved, so first of all the string should be used for recyclerView
        SharedPreferences substitutionPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String value = substitutionPreference.getString("parsedList", "noValue");

        if(!value.equals("noValue") && substitutionRefresh) {
            substitutionRefresh = false;
            recyclerView.setAdapter(new RecyclerAdapter(clearString(value), mainAct, context));
        }

        //get the current week
        Calendar calender = Calendar.getInstance();
        int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);

        //create the PrefsFragment in SettingsActivity to get a
        //PreferenceFragment and read the value of list
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String classNumber = pref.getString("classList","0");
        String attach;

        String urlSubCurrent;
        String urlSubNext = " ";

        //TODO TEST PURPOSE
        String className = substitutionPreference.getString("class", "");
        if(className.equals("TEST")){
            urlSubCurrent = "http://monkey-179.getforge.io/w/36/w00019.htm";
            urlSubNext = "http://monkey-179.getforge.io/w/37/w00019.htm";
        }
        else {
            //system expects format like '00', '01', .. '199' etc.
            if (classNumber.length() == 2) {
                attach = "0" + classNumber;
            } else if(classNumber.length() == 1){
                attach = "00" + classNumber;
            } else{
                attach = classNumber;
            }

            //this URL with the class attachment will be parsed
            urlSubCurrent = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/" + currentWeek + "/w00" + attach + ".htm";

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            //on the day after tuesday of the current week, there could be a plan for next week
            if (day != Calendar.MONDAY && day != Calendar.TUESDAY) {
                urlSubNext = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/" + (currentWeek + 1) + "/w00" + attach + ".htm";
            }
        }

        //for this and next week
        String url = urlSubCurrent + " , " + urlSubNext;

        //get Userdata of LoginPreference
        SharedPreferences login = context.getSharedPreferences("Login", 0);
        String user = login.getString("Unm","");
        String pass = login.getString("Psw","");

        //receive the result fired from async class of onPostExecute(result) method
        new SubParserClass(new SubParserClass.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //Preference for substitution values
                SharedPreferences parsedList = PreferenceManager.getDefaultSharedPreferences(context);

                //checks if output was created and is not equal to saved value
                if(output != null && !output.equals(parsedList.getString("parsedList", "noValue"))) {
                    SharedPreferences.Editor edit = parsedList.edit();
                    edit.putString("parsedList", output);
                    edit.apply();

                    ArrayList<String> newList = clearString(output);

                    recyclerView.setItemViewCacheSize(30);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    recyclerView.setAdapter(new RecyclerAdapter(newList, mainAct, context) {
                    });

                    runLayoutAnimation(recyclerView);
                }
                else if(!substitutionRefresh && !firstStart){
                    runLayoutAnimation(recyclerView);
                }

                firstStart = false;
                mainAct.stopReloading();
            }
        }).execute(url, user, pass); //start parser with following parameter
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private ArrayList<String> clearString(String value){
        String[] date = new String[10];

        String[] splitOutput = value.split(" SPLIT ");
        ArrayList<String> parsedListCurrent = new ArrayList<>(
                Arrays.asList(splitOutput[0].split("\\[ Montag ]"))); //split by [ Montag ] to prevent wrong splits

        //extend formatting
        for (int i = 1; i < 7; i++) {
            String x = parsedListCurrent.get(i - 1);

            if (i == 1) {
                //date of the first day (day 0) (no information)
                x = x.replaceAll("Untis 2017  4  Eduard-Spranger-Berufskolleg Hamm  1", "");
                x = x.replaceFirst(" ", "");

                //date
                String[] y0 = x.split(" ");
                date[0] = (y0[y0.length - 1]).trim();
            } else if (i == 2) {
                //day 1 information
                x = x.replaceAll("\\|","");
                x = x.replaceAll("\\[","");
                x = x.replaceAll("]","");
                x = x.replaceFirst(" Dienstag ", "");
                x = x.replaceFirst(" Mittwoch ", "");
                x = x.replaceFirst(" Donnerstag ", "");
                x = x.replaceFirst(" Freitag ", "");
                x = x.replaceAll("\\u00A0", ""); //special html character appears like a space
                x = x.trim(); //trims space on begin and end

                if(!x.contains("Nachrichten zum Tag")){
                    x = x.replaceFirst("~ ","");
                }

                parsedListCurrent.remove(i - 2);
                parsedListCurrent.add(i - 2, x);

                Log.d("ESBLOG", "Show me list of day " + (i-1) + ": " + x);
            } else {
                //day 2 - 5 information
                x = x.replaceFirst(" ", "");
                x = x.replaceAll("\\|","");
                x = x.replaceAll("\\[","");
                x = x.replaceAll("]","");
                x = x.replaceFirst(" Dienstag ", "");
                x = x.replaceFirst(" Mittwoch ", "");
                x = x.replaceFirst(" Donnerstag ", "");
                x = x.replaceFirst(" Freitag ", "");
                x = x.replaceAll("\\u00A0", ""); //special html character appears like a space

                //date
                String[] split = x.split("\\.");
                date[i - 2] = (split[0] + "." + split[1] + ".").trim();

                //replace the date (only information is left)
                x = x.replaceFirst(date[i - 2], "");

                if(!x.contains("Nachrichten zum Tag")){
                    x = x.replaceFirst("~ ","");
                }
                if(i == 6){ //Last part -> remove 2. HJ 16/17 ab 19.6. 14.7.2017
                    String splitter[] = x.split(" 1\\. HJ | 2\\. HJ ");
                    x = splitter[0]; //remove last separated part, splitter[1] contains e.g. 2. HJ 16/17 ab 19.6. 14.7.2017
                    x = x.trim();
                }
                x = x.trim();
                parsedListCurrent.remove(i - 2);
                parsedListCurrent.add(i - 2, x);

                Log.d("ESBLOG", "Show me list of day " + (i-1) + ": " + x);
            }
        }//list 0 -> date | list 1 - 5 -> info

        //cluster which isn't needed
        parsedListCurrent.remove(5);

        if(value.contains("SPLIT")) { //only next week, if split is available

            ArrayList<String> parsedListNext = new ArrayList<>(
                    Arrays.asList(splitOutput[1].split("\\[ Montag ]"))); //split by [ Montag ] to prevent wrong splits

            //extend formatting
            for (int i = 1; i < 7; i++) {
                String x = parsedListNext.get(i - 1);

                if (i == 1) {
                    //date of the first day (day 0) (no information)
                    x = x.replaceAll("Untis 2017  4  Eduard-Spranger-Berufskolleg Hamm  1", "");
                    x = x.replaceFirst(" ", "");

                    //date
                    String[] y0 = x.split(" ");
                    date[5] = (y0[y0.length - 1]).trim();
                } else if (i == 2) {
                    //day 1 information
                    x = x.replaceAll("\\|", "");
                    x = x.replaceAll("\\[", "");
                    x = x.replaceAll("]", "");
                    x = x.replaceFirst(" Dienstag ", "");
                    x = x.replaceFirst(" Mittwoch ", "");
                    x = x.replaceFirst(" Donnerstag ", "");
                    x = x.replaceFirst(" Freitag ", "");
                    x = x.replaceAll("\\u00A0", ""); //special html character appears like a space
                    x = x.trim(); //trims space on begin and end

                    if (!x.contains("Nachrichten zum Tag")) {
                        x = x.replaceFirst("~ ", "");
                    }

                    parsedListNext.remove(i - 2);
                    parsedListNext.add(i - 2, x);

                    Log.d("ESBLOG", "Show me list of day " + (i - 1) + ": " + x);
                } else {
                    //day 2 - 5 information
                    x = x.replaceFirst(" ", "");
                    x = x.replaceAll("\\|", "");
                    x = x.replaceAll("\\[", "");
                    x = x.replaceAll("]", "");
                    x = x.replaceFirst(" Dienstag ", "");
                    x = x.replaceFirst(" Mittwoch ", "");
                    x = x.replaceFirst(" Donnerstag ", "");
                    x = x.replaceFirst(" Freitag ", "");
                    x = x.replaceAll("\\u00A0", ""); //special html character appears like a space

                    //date
                    String[] split = x.split("\\.");
                    date[i + 3] = (split[0] + "." + split[1] + ".").trim();

                    //replace the date (only information is left)
                    x = x.replaceFirst(date[i + 3], "");

                    if (!x.contains("Nachrichten zum Tag")) {
                        x = x.replaceFirst("~ ", "");
                    }
                    if (i == 6) { //Last part -> remove 2. HJ 16/17 ab 19.6. 14.7.2017
                        String splitter[] = x.split(" 1\\. HJ | 2\\. HJ ");
                        x = splitter[0]; //remove last separated part, splitter[1] contains e.g. 2. HJ 16/17 ab 19.6. 14.7.2017
                        x = x.trim();
                    }
                    x = x.trim();
                    parsedListNext.remove(i - 2);
                    parsedListNext.add(i - 2, x);

                    Log.d("ESBLOG", "Show me list of day " + (i - 1) + ": " + x);
                }
            }//list 0 -> date | list 5 - 10 -> info

            //cluster which isn't needed
            parsedListNext.remove(5);

            //add all lines of the nextWeek - list into current list
            parsedListCurrent.addAll(parsedListNext);
        }

        //separate every date with a comma to save it as a string
        StringBuilder dateBuilder = new StringBuilder();
        for (String n : date) {
            dateBuilder.append(n + ",");
        }
        dateBuilder.deleteCharAt(dateBuilder.length() - 1);
        parsedListCurrent.add(0, dateBuilder.toString());

        String withoutRedun;
        //check the redundancy for every day and replace
        for(int i = 1; i < parsedListCurrent.size(); i++){
            if(!parsedListCurrent.get(i).contains("Vertretungen sind nicht freigegeben") &&
                    !parsedListCurrent.get(i).contains("Keine Vertretungen")){

                withoutRedun = eliminateRedundancy(parsedListCurrent.get(i));
                parsedListCurrent.remove(i);
                parsedListCurrent.add(i, withoutRedun);
            }
        }

        return parsedListCurrent;
    }

    //in some cases there are redundant listings so they can be
    //summarized in one card, the function checks if the case is the same
    private String eliminateRedundancy(String checkString){

        String info[] = checkString.split(" ~ ");
        int counter = info.length;

        if(counter < 2){
            return checkString; // no redundancy -> return parameter
        }
        else{
            String check1[], check2[], check3[], check4[];
            String newHours1, newHours2;

            if(counter == 2){
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);

                //they have same info
                if(check1[3].equals(check2[3])){
                    newHours1 = " " + check1[2] + " - " + check2[2] + " "; //important spaces because we delete the old in the replace process
                    return info[0].replace(" " + check1[2] + " ", newHours1); //replace old String hours with new one
                }
                else{
                    return checkString; //no redundancies -> return parameter
                }
            }
            else if(counter == 3) {
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                info[2] = info[2].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);
                check3 = info[2].split(" ", 4);

                //they have same info
                if (check1[3].equals(check2[3])) {
                    newHours1 = " " + check1[2] + " - " + check2[2] + " ";
                    return info[0].replace(" " + check1[2] + " ", newHours1) + " ~ " + info[2];
                } else if (check2[3].equals(check3[3])) {
                    newHours1 = " " + check2[2] + " - " + check3[2] + " ";
                    return info[0] + " ~ " + info[1].replace(" " + check2[2] + " ", newHours1);
                } else if (check1[3].equals(check2[3]) && check2[3].equals(check3[3])) {
                    newHours1 = " " + check1[2] + " - " + check2[2] + " ";
                    return info[0].replace(" " + check1[2] + " ", newHours1); //replace old String hours with new one
                } else {
                    return checkString;
                }
            }
            else if(counter == 4){
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                info[2] = info[2].trim();
                info[3] = info[3].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);
                check3 = info[2].split(" ", 4);
                check4 = info[3].split(" ", 4);

                //they have same info
                if (check1[3].equals(check2[3]) && check2[3].equals(check3[3])) {
                    newHours1 = " " + check1[2] + " - " + check2[2] + " ";
                    return info[0].replace(" " + check1[2] + " ", newHours1); //replace old String hours with new one
                } else if(check1[3].equals(check2[3]) && check3[3].equals(check4[3])){
                    newHours1 = " " + check1[2] + " - " + check2[2] + " ";
                    newHours2 = " " + check3[2] + " - " + check4[2] + " ";
                    return info[0].replace(" " + check1[2] + " ", newHours1) + " ~ " + info[2].replace(" " + check3[2] + " ", newHours2);
                } else if (check1[3].equals(check2[3])) {
                    newHours1 = " " + check1[2] + " - " + check2[2] + " ";
                    return info[0].replace(" " + check1[2] + " ", newHours1) + " ~ " + info[2] + " ~ " + info[3];
                } else if (check2[3].equals(check3[3])) {
                    newHours1 = " " + check2[2] + " - " + check3[2] + " ";
                    return info[0] + " ~ " + info[1].replace(" " + check2[2] + " ", newHours1) + " ~ " + info[3];
                } else if (check3[3].equals(check4[3])) {
                    newHours1 = " " + check3[2] + " - " + check4[2] + " ";
                    return info[0] + " ~ " + info[1] +  " ~ " + info[2].replace(" " + check3[2] + " ", newHours1);
                } else {
                    return checkString;
                }
            }
            return checkString;
        }
    }
}
