package com.ronschka.david.esb.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.ronschka.david.esb.MainActivity;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.helper.TimeConnectionClass;
import com.ronschka.david.esb.helper.TimetableAdapter;
import com.ronschka.david.esb.helper.Timetable_Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimetableClass {

    final private Context context;
    private RecyclerView recyclerView;
    private MainActivity mainActivity;
    private int spacing;
    private StaggeredGridLayoutManager lLayout;

    public TimetableClass(Context context, RecyclerView recyclerView, int spacing, int cardWidth, MainActivity mainActivity) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.mainActivity = mainActivity;
        this.spacing = spacing;
        recyclerView.setFocusable(false);
        getConnection(true, cardWidth);
    }

    public void rebuild(int cardWidth){
        getConnection(false, cardWidth);
    }

    public void getConnection(final boolean initial, final int cardWidth){
        //parsedList is saved, so first of all the string should be used for recyclerView
        SharedPreferences substitutionPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String savedSub = substitutionPreference.getString("parsedTime", "noValue");

        if(!savedSub.equals("noValue")) {
            List<String> savedArray = new ArrayList<>(
                    Arrays.asList(savedSub.split(" HOURSPLIT ")));
            recyclerView.setVisibility(View.GONE);
            List<Timetable_Item> rowListItem = getAllItemList(savedArray);
            recyclerView.setAdapter(new TimetableAdapter(context, rowListItem, cardWidth, mainActivity, recyclerView));
            runLayoutAnimation(recyclerView);
        }

        //get the current week
        Calendar calender = Calendar.getInstance();
        int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);

        //create the PrefsFragment in SettingsActivity to get the class value of the list
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String classNumber = pref.getString("classList","0");
        String attach;

        String urlTimetable;

        //system expects format like '00', '01', .. '199' etc.
        if (classNumber.length() == 2) {
            attach = "0" + classNumber;
        } else if(classNumber.length() == 1){
            attach = "00" + classNumber;
        } else{
            attach = classNumber;
        }

        //TODO test run
        currentWeek = 42;

        //this URL with the class attachment will be parsed
        urlTimetable = "http://www.esb-hamm.de/vertretungsplan/infosystemschueler/infosystemschueler" +
                "/infosystemschueler/c/" + currentWeek + "/c00" + attach + ".htm";

        //get Userdata of LoginPreference
        SharedPreferences login = context.getSharedPreferences("Login", 0);
        String user = login.getString("Unm","");
        String pass = login.getString("Psw","");

        //receive the result fired from async class of onPostExecute(result) method
        new TimeConnectionClass(new TimeConnectionClass.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //Preference for substitution values
                SharedPreferences parsedList = PreferenceManager.getDefaultSharedPreferences(context);

                //checks if output was created and is not equal to saved value
                if(output != null && !output.equals(parsedList.getString("parsedList", "noValue"))) {
                    SharedPreferences.Editor edit = parsedList.edit();
                    edit.putString("parsedTime", output);
                    edit.apply();

                    List<String> outputArray = new ArrayList<>(
                            Arrays.asList(output.split(" HOURSPLIT ")));

                    for (int x = 1; x < outputArray.size(); x++) {
                        Log.d("ESBLOG", "OUTPUT ARRAY: " + outputArray.get(x));
                    }

                    if (initial) {
                        List<Timetable_Item> rowListItem = getAllItemList(outputArray);
                        lLayout = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(lLayout);

                        TimetableAdapter timetableAdapter = new TimetableAdapter(context, rowListItem, cardWidth, mainActivity, recyclerView);
                        recyclerView.setFocusable(false);
                        recyclerView.setNestedScrollingEnabled(false);
                        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
                        recyclerView.setAdapter(timetableAdapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        List<Timetable_Item> rowListItem = getAllItemList(outputArray);
                        recyclerView.setAdapter(new TimetableAdapter(context, rowListItem, cardWidth, mainActivity, recyclerView));
                        runLayoutAnimation(recyclerView);
                    }
                }
            }
        }).execute(urlTimetable, user, pass); //start parser with following parameter
    }

    private List<Timetable_Item> getAllItemList(List<String> input){

        List<Timetable_Item> allItems = new ArrayList<>();
        String[] splitterString, splitterString2, splitterString3;

        for(int x = 1; x < input.size(); x++){
           splitterString = input.get(x).split(" SPLIT ");
            for(int y = 1; y < splitterString.length; y++){
                if(splitterString[y].equals("1) ~ ") || splitterString[y].equals("2) ~ ") ||
                        splitterString[y].equals("3) ~ ") || splitterString[y].equals("4) ~ ") ||
                        splitterString[y].equals("5) ~ ") || splitterString[y].equals("6) ~ ") ||
                        splitterString[y].equals("7) ~ ") || splitterString[y].equals("8) ~ ") ||
                        splitterString[y].equals("9) ~ ") || splitterString[y].equals("10) ~ ") ||
                        splitterString[y].equals("11) ~ ") || splitterString[y].equals("12) ~ ")){
                    splitterString[y] = "";
                }
                if(!splitterString[y].isEmpty()) {
                    splitterString2 = splitterString[y].split(" ~ ");
                    int hours;
                    if (splitterString2.length > 1) {
                        hours = Integer.parseInt(splitterString2[1]);
                    } else {
                        hours = 0;
                    }
                    if (splitterString2[0].split(" ").length == 3) { //normal case e.g. M KUN H301
                        splitterString3 = splitterString2[0].split(" ");

                        allItems.add(new Timetable_Item(splitterString3[0], splitterString3[2], splitterString3[1],
                                hours / 2, context));
                    }else if(splitterString2[0].contains("Veranstaltung")){
                        allItems.add(new Timetable_Item(splitterString2[0], " ", "",
                                hours / 2, context));
                    }
                    else {
                        allItems.add(new Timetable_Item(splitterString2[0], "", "",
                                hours / 2, context));
                    }
                    Log.d("ESBLOG", "SHOW SPLITTER:" + " " + x + " " + splitterString[y]);
                }
            }
        }

        /*
        allItems.add(new Timetable_Item("SP", "T1", 2, context.getResources().getString(0+ R.color.MaterialCyan)));
        allItems.add(new Timetable_Item("M", "H309", 2, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("E", "H309", 1, context.getResources().getString(0+ R.color.MaterialGreen)));
        allItems.add(new Timetable_Item("E", "H301", 2, context.getResources().getString(0+ R.color.MaterialGreen)));
        allItems.add(new Timetable_Item("M", "E104", 2, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("M", "H309", 1, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("S", "E305", 2, context.getResources().getString(0+ R.color.MaterialDeepOrange)));
        allItems.add(new Timetable_Item("WW", "H309", 2, context.getResources().getString(0+ R.color.MaterialPink)));
        allItems.add(new Timetable_Item("PHY", "E302", 2, context.getResources().getString(0+ R.color.MaterialTeal)));
        allItems.add(new Timetable_Item("TI", "H003", 2, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("S", "E202", 2, context.getResources().getString(0+ R.color.MaterialDeepOrange)));
        allItems.add(new Timetable_Item("D", "H301", 2, context.getResources().getString(0+ R.color.MaterialRed)));
        allItems.add(new Timetable_Item("INF", "H113", 1, context.getResources().getString(0+ R.color.MaterialAmber)));
        allItems.add(new Timetable_Item("INF", "H113", 2, context.getResources().getString(0+ R.color.MaterialAmber)));
        allItems.add(new Timetable_Item("REL", "H311", 2, context.getResources().getString(0+ R.color.MaterialPurple)));
        allItems.add(new Timetable_Item("TI", "E204", 2, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("TI", "H113", 1, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("", "", 0, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("GMG", "H309", 2, context.getResources().getString(0+ R.color.MaterialPink)));
        allItems.add(new Timetable_Item("D", "H309", 1, context.getResources().getString(0+ R.color.MaterialRed)));
        allItems.add(new Timetable_Item("", "", 0, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("ET", "E302", 2, context.getResources().getString(0+ R.color.MaterialLightGreen)));
        */
        return allItems;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.grid_animation);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
            outRect.top = space/2;
            outRect.bottom = space/2;
        }
    }
}
