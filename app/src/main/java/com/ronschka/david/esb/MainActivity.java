package com.ronschka.david.esb;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ronschka.david.esb.fragments.exam_fragment;
import com.ronschka.david.esb.fragments.homework_fragment;
import com.ronschka.david.esb.fragments.substitution_fragment;
import com.ronschka.david.esb.fragments.timetable_fragment;
import com.ronschka.david.esb.helper.Homework;

public class MainActivity extends AppCompatActivity{

    private int previousChild; //previousChild view
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);

        if(!previouslyStarted) {
            //opens php class for string echo with class, teacher and room information
            PhpClass php = new PhpClass();
            php.setContext(getApplicationContext());
            php.execute();

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();
        }
        else{
            setContentView(R.layout.activity_main);

            //bottom navigation interface
            bottomNavigationView = findViewById(R.id.navigation);

            if (savedInstanceState != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
                int navigationPosition = savedInstanceState.getInt("NAVIGATION_NUMBER");
                switch(flipperPosition){
                    case R.id.navigation_plan:
                        fragmentTransaction.replace(R.id.content, new timetable_fragment()).commit();
                        break;
                    case R.id.navigation_substitution:
                        fragmentTransaction.replace(R.id.content, new substitution_fragment()).commit();
                        break;
                    case R.id.navigation_homework:
                        fragmentTransaction.replace(R.id.content, new homework_fragment()).commit();
                        break;
                    case R.id.navigation_exam:
                        fragmentTransaction.replace(R.id.content, new exam_fragment()).commit();
                        break;
                }
                bottomNavigationView.setSelectedItemId(navigationPosition);
                previousChild = savedInstanceState.getInt("TAB_NUMBER");
            }
            else{
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, new timetable_fragment()).commit();
                //start is always navigation_plan
                previousChild = R.id.navigation_plan;
            }

            setupEverything();
        }
    }

    private void setupEverything(){
        //TODO doesn't need to check every start
        //opens php class for string echo with class, teacher and room information
        PhpClass php = new PhpClass();
        php.setContext(getApplicationContext());
        php.execute();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //switch views
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (previousChild != item.getItemId()) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.fragment, R.anim.fade_out);
                    //TODO just a simple animation (change later)
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    //switching tab means you don't need previous progress bar
                    progressBar.setVisibility(View.GONE);
                    switch ((item.getItemId())) {
                        case R.id.navigation_plan:
                            previousChild = R.id.navigation_plan;
                            fragmentTransaction.replace(R.id.content, new timetable_fragment()).commit();
                            break;
                        case R.id.navigation_substitution:
                            previousChild = R.id.navigation_substitution;
                            fragmentTransaction.replace(R.id.content, new substitution_fragment()).commit();
                            break;
                        case R.id.navigation_homework:
                            previousChild = R.id.navigation_homework;
                            fragmentTransaction.replace(R.id.content, new homework_fragment()).commit();
                            break;
                        case R.id.navigation_exam:
                            previousChild = R.id.navigation_exam;
                            fragmentTransaction.replace(R.id.content, new exam_fragment()).commit();
                            break;
                    }
                }
                else{
                    //scrolls up if selected navigation become touched again
                    switch (previousChild) {
                        case R.id.navigation_plan:
                            break;
                        case R.id.navigation_substitution:
                            //substitutionRecycler.smoothScrollToPosition(0);
                            break;
                        case R.id.navigation_homework:
                            //hwList.smoothScrollToPosition(0);
                            break;
                        case R.id.navigation_exam:
                            break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        int navigation = bottomNavigationView.getSelectedItemId();
        savedInstanceState.putInt("TAB_NUMBER", previousChild);
        savedInstanceState.putInt("NAVIGATION_NUMBER", navigation);
    }

    private void updateClassname(){
        //saved className
        SharedPreferences classNamePreference = getSharedPreferences("className", 0);
        String className = classNamePreference.getString("class", "");

        //attach the class to toolbar
        if(!className.equals("")){
            getSupportActionBar().setTitle(getString(R.string.app_name) + " - " + className);
        }
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
                edit.putString("class", returnedResult);
                edit.apply();
            }
        }
    }

    public void stopReloading() {
        //turn refresher off
        //swipeContainerSub.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public final void onCreateContextMenu(final ContextMenu menu, final View v,
                                          final ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 1, getString(R.string.dialog_edit));
        menu.add(0, v.getId(), 2, getString(R.string.dialog_delete));
    }

    @Override
    public final void onResume() {
        super.onResume();
        updateClassname();
    }

    private void deleteAll() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog
                .setTitle("Wirklich alles Löschen?")
                .setMessage("Alles Löschen?")
                .setPositiveButton((getString(android.R.string.yes)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface d, final int i) {
                                Homework.delete(getApplicationContext(), null);
                                //updateHomework();
                            }
                        })
                .setNegativeButton((getString(android.R.string.no)), null)
                .show();
    }
}
