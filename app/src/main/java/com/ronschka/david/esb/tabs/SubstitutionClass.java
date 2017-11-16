package com.ronschka.david.esb.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.ronschka.david.esb.R;
import com.ronschka.david.esb.helper.SubConnectionClass;
import com.ronschka.david.esb.helper.SubstitutionAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class SubstitutionClass{

    private boolean substitutionRefresh;
    private boolean firstStart;
    final private Context context;
    final private SwipeRefreshLayout swipeRefreshLayout;
    final private RecyclerView recyclerView;

    public SubstitutionClass(Context context, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView){
        substitutionRefresh = true;
        firstStart = true;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView = recyclerView;
    }

    public void parseSubstitution(){
        //parsedList is saved, so first of all the string should be used for recyclerView
        SharedPreferences substitutionPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String savedSub = substitutionPreference.getString("parsedList", "noValue");

        if(!savedSub.equals("noValue") && substitutionRefresh) {
            String[] splitter = savedSub.split(" DATESPLIT ");

            ArrayList<String> subList = new ArrayList<>(
                    Arrays.asList(splitter[0].split(" DAYSPLIT ")));

            subList.add(subList.size(), splitter[1]); //date information

            substitutionRefresh = false;
            recyclerView.setAdapter(new SubstitutionAdapter(subList, context));
        }

        //get the current week
        Calendar calender = Calendar.getInstance();
        int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);

        //create the PrefsFragment in SettingsActivity to get the class value of the list
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String classNumber = pref.getString("classList","0");
        String attach;

        String urlSubCurrent;
        String urlSubNext;

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
            urlSubCurrent = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis" +
                    "/w/" + currentWeek + "/w00" + attach + ".htm";

            //there could be a plan for next week
            urlSubNext = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis" +
                    "/w/" + (currentWeek + 1) + "/w00" + attach + ".htm";
        }

        //for this and next week
        String url = urlSubCurrent + " , " + urlSubNext;

        //get Userdata of LoginPreference
        SharedPreferences login = context.getSharedPreferences("Login", 0);
        String user = login.getString("Unm","");
        String pass = login.getString("Psw","");

        //receive the result fired from async class of onPostExecute(result) method
        new SubConnectionClass(new SubConnectionClass.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //Preference for substitution values
                SharedPreferences parsedList = PreferenceManager.getDefaultSharedPreferences(context);

                //checks if output was created and is not equal to saved value
                if(output != null && !output.equals(parsedList.getString("parsedList", "noValue"))) {
                    SharedPreferences.Editor edit = parsedList.edit();
                    edit.putString("parsedList", output);
                    edit.apply();

                    String[] splitter = output.split(" DATESPLIT ");

                    ArrayList<String> subList = new ArrayList<>(
                            Arrays.asList(splitter[0].split(" DAYSPLIT ")));

                    subList.add(subList.size(), splitter[1]); //date information
                    recyclerView.setItemViewCacheSize(30);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    recyclerView.setAdapter(new SubstitutionAdapter(subList, context) {
                    });

                    runLayoutAnimation(recyclerView);
                }
                else if(!substitutionRefresh && !firstStart){
                    runLayoutAnimation(recyclerView);
                }

                firstStart = false;
                swipeRefreshLayout.setRefreshing(false);
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

    //in some cases there are redundant listings so they can be
    //summarized in one card, the function checks if the case is the same
    //TODO overwrite it
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
