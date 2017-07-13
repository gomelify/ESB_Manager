package com.ronschka.david.esb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity{

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //Animations
       /* final Animation fab_sync = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_sync);

        //FloatingButton
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //RefreshButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get ClassValue of ClassPreference
                SharedPreferences className = getSharedPreferences("className", 0);
                String classNameString = className.getString("class","");

                parseUrl(classNameString);
                fab.startAnimation(fab_sync);
            }
        }); */

        //get ClassValue of ClassPreference
        SharedPreferences className = getSharedPreferences("className", 0);
        String classNameString = className.getString("class","");
        //and start parsing
        parseUrl(classNameString);
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

        dialog.show();
        return true;
    }

    public void parseUrl(String className){
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);

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
        //String url = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/28/w000" + attach + ".htm";
        //String url = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/28/w00000.htm";

        //local
        String url = "http://hat-218.getforge.io/w/28/w000" + attach + ".htm";

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
                            Arrays.asList(output.split("Montag")));

                    //check all splitted strings separately
                    for (int b = 0; b < 6; b++) {
                        String test = parsedList.get(b);
                        Log.d("ESBLOG", "Value of the String: " + test);
                    }

                    String[] date = new String[5];

                    //extend formatting
                    for (int i = 1; i < 7; i++) {
                        String x = parsedList.get(i - 1);

                        if (i == 1) {
                            //date of the first day (day 0) (no information)
                            x = x.replaceAll("Untis 20176Eduard-Spranger-Berufskolleg Hamm1", "");
                            x = x.replaceFirst(" ", "");
                            x = x.replaceAll("   ", " ");

                            //date
                            String[] y0 = x.split(" ");
                            date[0] = y0[y0.length - 1];
                        } else if (i == 2) {
                            //day 1 information
                            x = x.replaceFirst(" ", "");
                            x = x.replaceAll("  ", "");
                            parsedList.remove(i - 2);
                            parsedList.add(i - 2, x);

                            Log.d("ESBLOG", "Show me list of day " + (i-1) + ": " + x);
                        } else {
                            //day 2 - 5 information
                            x = x.replaceFirst(" ", "");

                            //date
                            String[] split = x.split("\\.");
                            date[i - 2] = split[0] + "." + split[1] + ".";

                            //replace the date (only information is left)
                            x = x.replaceFirst(date[i - 2], "");

                            x = x.replaceAll("  ", "");

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

                    recyclerView.setAdapter(new RecyclerAdapter(parsedList) {
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint(getString(R.string.searchViewHint));

        //text listener for the searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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

}
