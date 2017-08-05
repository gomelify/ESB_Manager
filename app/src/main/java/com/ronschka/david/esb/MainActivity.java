package com.ronschka.david.esb;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity{

    private SwipeRefreshLayout swipeContainer;
    private String classNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();
            Log.d("ESBLOG", "FIRST START!");
        }
        else{
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //opens php class for string echo with class, teacher and room information
            PhpClass php = new PhpClass();
            php.setContext(getApplicationContext());
            php.execute();

            //SwipeRefresher
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get ClassValue of ClassPreference
                    SharedPreferences className = getSharedPreferences("className", 0);
                    String classNameString = className.getString("class","");

                    parseUrl(classNameString);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorTextAccent);

            //get ClassValue of ClassPreference
            SharedPreferences className = getSharedPreferences("className", 0);
            classNameString = className.getString("class","");
            //and start parsing
            parseUrl(classNameString);
        }
    }

    public boolean onCreateLoginWindow() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_login, null);
        final EditText mUser = (EditText) mView.findViewById(R.id.edUsername);
        final EditText mPassword = (EditText) mView.findViewById(R.id.edPassword);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!mUser.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){

                    //LoginData Storage
                    SharedPreferences user_data = getSharedPreferences("Login", 0);
                    SharedPreferences.Editor edit = user_data.edit();

                    //Delete old data
                    edit.clear();
                    edit.apply();

                    //Put new data in
                    edit.putString("Unm",mUser.getText().toString());
                    edit.putString("Psw",mPassword.getText().toString());
                    edit.apply();

                    //get ClassValue of ClassPreference
                    SharedPreferences className = getSharedPreferences("className", 0);
                    String classNameString = className.getString("class","");
                    parseUrl(classNameString);
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(MainActivity.this,
                            R.string.login_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //up-down animation
        dialog.show();
        return true;
    }

    public void parseUrl(String className){
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);

        //get the current week
        Calendar calender = Calendar.getInstance();
        int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);

        if(!(swipeContainer.isRefreshing())){
            progressBar.setVisibility(View.VISIBLE);
        }

        //create the PrefsFragment in SettingsActivity to get a
        //PreferenceFragment and read the value of list
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String classNumber = pref.getString("classList","0");
        String attach;

        //attach the class to toolbar
        if(!className.equals("")){
            getSupportActionBar().setTitle(getString(R.string.app_name) + " - " + className);
        }

        //system expects format like '00', '01', .. '99' etc.
        if(classNumber.length() < 2){
            attach = "0" + classNumber;
        }
        else{
            attach = classNumber;
        }

        //this URL with the class attachment will be parsed
        //String url = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/" + currentWeek + "/w000" + attach + ".htm";
        //String url = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/28/w00000.htm";

        //for the internal tests
        String url = "http://jockisch-is-mr-hamster.getforge.io/28/w000" + attach + ".htm";

        //get Userdata of LoginPreference
        SharedPreferences login = getSharedPreferences("Login", 0);
        String user = login.getString("Unm","");
        String pass = login.getString("Psw","");

        //receive the result fired from async class of onPostExecute(result) method
        new ParserClass(new ParserClass.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //checks if output was created
                if(output != null) {

                    RecyclerView recyclerView;
                    ArrayList<String> parsedList = new ArrayList<>(
                            Arrays.asList(output.split("\\[ Montag ]"))); //split by [ Montag ] to prevent wrong splits

                    String[] date = new String[5];

                    //extend formatting
                    for (int i = 1; i < 7; i++) {
                        String x = parsedList.get(i - 1);

                        if (i == 1) {
                            //date of the first day (day 0) (no information)
                            x = x.replaceAll("Untis 20176Eduard-Spranger-Berufskolleg Hamm1", "");
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

                            parsedList.remove(i - 2);
                            parsedList.add(i - 2, x);

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
                            parsedList.remove(i - 2);
                            parsedList.add(i - 2, x);

                            Log.d("ESBLOG", "Show me list of day " + (i-1) + ": " + x);
                        }
                    }//list 0 -> date | list 1 - 5 -> info

                    //separate every date with a comma to save it as a string
                    StringBuilder dateBuilder = new StringBuilder();
                    for (String n : date) {
                        dateBuilder.append(n + ",");
                    }
                    dateBuilder.deleteCharAt(dateBuilder.length() - 1);
                    parsedList.add(0, dateBuilder.toString());

                    parsedList.remove(6);

                    recyclerView = (RecyclerView) findViewById(R.id.recycler);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    recyclerView.setAdapter(new RecyclerAdapter(parsedList, MainActivity.this) {
                    });
                }
                else{
                    onCreateLoginWindow();
                }

                //turn refresher off
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }).execute(url, user, pass); //start parser with following parameter
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName =
                new ComponentName(getApplicationContext(), SearchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(componentName));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            int requestCode = 1;
            startActivityForResult(settings, requestCode);
            return true;
        }
        else if (id == R.id.action_login) {
            onCreateLoginWindow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();

                //save the new classValue
                SharedPreferences className = getSharedPreferences("className", 0);
                SharedPreferences.Editor edit = className.edit();

                //Delete old data
                edit.clear();
                edit.apply();

                //Put new data in
                edit.putString("class",returnedResult);
                edit.apply();

                parseUrl(returnedResult);
            }
        }
    }

    public void onCreateDetailView(String detail){
        String detailArray[] = detail.split(",");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.activity_details, null);
        TextView txtCase = (TextView) mView.findViewById(R.id.textViewCase);
        TextView txtHours = (TextView) mView.findViewById(R.id.textViewHours);
        TextView txtDate = (TextView) mView.findViewById(R.id.txtDateDetail);
        TextView txtClass = (TextView) mView.findViewById(R.id.txtClassDetail);
        TextView txtInfo = (TextView)mView.findViewById(R.id.txtInfoDetail);
        TextView txtTeacher = (TextView)mView.findViewById(R.id.txtTeacherDetail);

        //set handed details
        txtCase.setText(detailArray[0]);
        txtCase.setTextColor(Color.parseColor(detailArray[2]));
        txtHours.setText(detailArray[1]);
        txtHours.setTextColor(Color.parseColor(detailArray[2]));
        txtDate.setText(detailArray[3] + "," + detailArray[4]); //two arrays because they are by default separated with a comma
        txtInfo.setText(detailArray[5]);
        txtTeacher.setText(detailArray[6]);

        txtClass.setText(classNameString);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //up-down animation
        dialog.show();

        final GestureDetector gdt = new GestureDetector(this, new GestureListener());

        //listener for fling gesture
        mView.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motion) {
                if(gdt.onTouchEvent(motion)){
                    dialog.dismiss();
                    return false;
                }
                return true;
            }
        });
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            if(distanceX > -25 && distanceX < 25 && distanceY < -8){ //Down-gesture
                return true;
            }
            else{
                return false;
            }
        }
    }
}
