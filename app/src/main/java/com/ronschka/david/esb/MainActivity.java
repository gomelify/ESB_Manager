package com.ronschka.david.esb;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ronschka.david.esb.databaseExams.SourceEx;
import com.ronschka.david.esb.databaseHomework.SourceHw;
import com.ronschka.david.esb.helper.Converter;
import com.ronschka.david.esb.helper.CustomAdapter;
import com.ronschka.david.esb.helper.Homework;
import com.ronschka.david.esb.tabs.SubstitutionClass;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private static ArrayList<HashMap<String, String>> hwArray = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> exArray = new ArrayList<>();
    private int previous = R.id.navigation_plan; //previous view
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ListView hwList;
    private ListView exList;

    //tabs
    private SubstitutionClass substitutionClass;

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
            setupEverything();
        }
    }

    private void setupEverything(){
        //TODO doesn't need to check every start
        //opens php class for string echo with class, teacher and room information
        PhpClass php = new PhpClass();
        php.setContext(getApplicationContext());
        php.execute();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set viewFlipper's animations
        final ViewFlipper viewFlipper = findViewById(R.id.viewFliper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.fade_out));
        viewFlipper.setDisplayedChild(0);

        //SwipeRefresher -> substitution
        swipeContainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorTextAccent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createSubstitutionView();
            }
        });

        progressBar = findViewById(R.id.progressBar);

        //homework list
        hwList = findViewById(R.id.listViewHomework);
        exList = findViewById(R.id.listViewExams);

        //RecyclerView -> substitution
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //initialize the tab classes
        substitutionClass = new SubstitutionClass(getApplicationContext(), MainActivity.this, recyclerView);

        final ScrollView scrollView = findViewById(R.id.scroller);

        //bottom navigation interface
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        //Animation
        final Animation fab_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_up);
        final Animation fab_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_down);
        //fab -> homework
        final FloatingActionButton fab = findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomNavigationView.getSelectedItemId() == R.id.navigation_homework){
                    startActivity(new Intent(getApplicationContext(), AddHomework.class));
                }
                else{
                    startActivity(new Intent(getApplicationContext(), AddExam.class));
                }
            }
        });

        //TODO ONLY FOR TEST PURPOSE!!
        //bottomNavigationView.setVisibility(View.GONE);
        //viewFlipper.setDisplayedChild(1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            //switch views
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (previous != item.getItemId()) {
                    switch ((item.getItemId())) {
                        case R.id.navigation_plan:
                            if(previous == R.id.navigation_homework || previous == R.id.navigation_exam){
                                fab.startAnimation(fab_hide);
                            }
                            fab.setVisibility(View.GONE);
                            previous = R.id.navigation_plan;
                            viewFlipper.setDisplayedChild(0);
                            break;
                        case R.id.navigation_substitution:
                            if(previous == R.id.navigation_homework || previous == R.id.navigation_exam){
                                fab.startAnimation(fab_hide);
                            }
                            fab.setVisibility(View.GONE);
                            viewFlipper.setDisplayedChild(1);
                            previous = R.id.navigation_substitution;
                            break;
                        case R.id.navigation_homework:
                            fab.setVisibility(View.VISIBLE);
                            if(!(previous == R.id.navigation_exam)){
                                fab.startAnimation(fab_show);
                            }
                            viewFlipper.setDisplayedChild(2);
                            previous = R.id.navigation_homework;
                            break;
                        case R.id.navigation_exam:
                            fab.setVisibility(View.VISIBLE);
                            if(!(previous == R.id.navigation_homework)){
                                fab.startAnimation(fab_show);
                            }
                            viewFlipper.setDisplayedChild(3);
                            previous = R.id.navigation_exam;
                            break;
                    }
                }
                else{
                    //scrolls up if selected navigation become touched again
                    switch (previous) {
                        case R.id.navigation_plan:
                            Log.d("ESBLOG", "TRIGGER!" + scrollView);
                            break;
                        case R.id.navigation_substitution:
                            recyclerView.smoothScrollToPosition(0);
                            break;
                        case R.id.navigation_homework:
                            hwList.smoothScrollToPosition(0);
                            break;
                        case R.id.navigation_exam:
                            break;
                    }
                }

                return true;
            }
        });
        createSubstitutionView();
        updateExams();
    }

    private void createSubstitutionView(){
        if(!(swipeContainer.isRefreshing())){
            progressBar.setVisibility(View.VISIBLE);
        }

        //saved className
        SharedPreferences classNamePreference = getSharedPreferences("className", 0);
        String className = classNamePreference.getString("class", "");

        //attach the class to toolbar
        if(!className.equals("")){
            getSupportActionBar().setTitle(getString(R.string.app_name) + " - " + className);
        }

        substitutionClass.parseSubstitution();
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

                createSubstitutionView();
            }
        }
    }

    //detail viewCards for substitution
    public void onCreateDetailView(String detail){
        String detailArray[] = detail.split("~");

        Log.d("ESBLOG", "CLICK: " + detail);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.activity_details, null);
        TextView txtCase = mView.findViewById(R.id.textViewCase);
        TextView txtHours = mView.findViewById(R.id.textViewHours);
        TextView txtDate = mView.findViewById(R.id.txtDateDetail);
        TextView txtClass = mView.findViewById(R.id.txtClassDetail);
        TextView txtInfo = mView.findViewById(R.id.txtInfoDetail);
        TextView txtTeacher = mView.findViewById(R.id.txtTeacherDetail);
        TextView txtRoom = mView.findViewById(R.id.txtRoomDetail);

        // Array explanation 0 -> head, 1 -> hours, 2 -> color, 3 -> Date, 4 -> Info, 5 -> Teacher, 6 -> Room

        //set handed details
        txtCase.setText(detailArray[0]);
        txtCase.setTextColor(Color.parseColor(detailArray[2]));
        txtHours.setText(detailArray[1]);
        txtHours.setTextColor(Color.parseColor(detailArray[2]));
        txtDate.setText(detailArray[3]);
        txtInfo.setText(detailArray[4]);
        txtTeacher.setText(detailArray[5]);
        txtRoom.setText(detailArray[6]);

        //saved className
        SharedPreferences classNamePreference = getSharedPreferences("className", 0);
        String classNameString = classNamePreference.getString("class", "");
        txtClass.setText(classNameString);

        if(detailArray[0].equals("Nachricht des Tages")){
            txtCase.setText("Nachricht");
        }

        if(detailArray[4].equals(" ")){
            mView.findViewById(R.id.imgDescription).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewDescription).setVisibility(View.GONE);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeDescription);
                relative.setVisibility(View.GONE);
            }
        }

        if(detailArray[5].equals(" ")){
            mView.findViewById(R.id.imgTeacher).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewTeacher).setVisibility(View.GONE);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeTeacher);
                relative.setVisibility(View.GONE);
            }
        }

        if(detailArray[6].equals(" ")){
            mView.findViewById(R.id.imgRoom).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewRoom).setVisibility(View.GONE);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeRoom);
                relative.setVisibility(View.GONE);
            }
        }
        if(detailArray[1].length() > 4){
            txtHours.setTextSize(28);
        }

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

    public void stopReloading() {
        //turn refresher off
        swipeContainer.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            //Down-gesture
            return distanceX > -25 && distanceX < 25 && distanceY < -8;
        }
    }

    @Override
    public final void onCreateContextMenu(final ContextMenu menu, final View v,
                                          final ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d("ESBLOG", "Test2: " + v.getId());
        menu.add(0, v.getId(), 1, getString(R.string.dialog_edit));
        menu.add(0, v.getId(), 2, getString(R.string.dialog_delete));
    }

    @Override
    public final boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle() == getString(R.string.dialog_edit)) {
            if(item.getItemId() == R.id.listViewHomework){
                editOne(hwArray, info.position, true); //true is homework type
            }
            else{
                editOne(exArray, info.position, false);
            }
            return true;
        }
        if (item.getTitle() == getString(R.string.dialog_delete)) {
            if(item.getItemId() == R.id.listViewHomework){
                deleteOne(hwArray, info.position, true); //true is homework type
            }
            else{
                deleteOne(exArray, info.position, false);
            }
            return true;
        }
        return false;
    }

    private void editOne(final ArrayList<HashMap<String, String>> Array, final int pos, boolean type) {
        final String currentID;
        final Intent intent;
        final Bundle mBundle;
        if(type) {
            currentID = "ID = " + Array.get(pos).get(SourceHw.allColumns[0]);
            intent = new Intent(this, AddHomework.class);
            mBundle = new Bundle();
            mBundle.putString(SourceHw.allColumns[0], currentID);
            for (int i = 1; i < SourceHw.allColumns.length; i++)
                mBundle.putString(SourceHw.allColumns[i],
                        Array.get(pos).get(SourceHw.allColumns[i]));
        }
        else{
            currentID = "ID = " + Array.get(pos).get(SourceEx.allColumns[0]);
            intent = new Intent(this, AddExam.class);
            mBundle = new Bundle();
            mBundle.putString(SourceEx.allColumns[0], currentID);
            for (int i = 1; i < SourceEx.allColumns.length; i++)
                mBundle.putString(SourceEx.allColumns[i],
                        Array.get(pos).get(SourceEx.allColumns[i]));
        }
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    private void deleteOne(final ArrayList<HashMap<String, String>> ArHa, final int pos, boolean type) {
        final ArrayList<HashMap<String, String>> tempArray = Converter.toTmpArray(ArHa, pos);

        if(type) {
            final String currentID = "ID = " + ArHa.get(pos).get(SourceHw.allColumns[0]);
            final SimpleAdapter alertAdapter = CustomAdapter.entry(this, tempArray, true);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog
                    .setTitle(R.string.delete_homework)
                    .setAdapter(alertAdapter, null)
                    .setPositiveButton((getString(android.R.string.yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface d, final int i) {
                                    Homework.delete(getApplicationContext(), currentID);
                                    updateHomework();
                                }
                            })
                    .setNegativeButton((getString(android.R.string.no)), null)
                    .show();
        }
        else{
            final String currentID = "ID = " + ArHa.get(pos).get(SourceEx.allColumns[0]);
            final SimpleAdapter alertAdapter = CustomAdapter.entry(this, tempArray, true);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog
                    .setTitle(R.string.delete_exam)
                    .setAdapter(alertAdapter, null)
                    .setPositiveButton((getString(android.R.string.yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface d, final int i) {
                                    Homework.delete(getApplicationContext(), currentID);
                                    updateExams();
                                }
                            })
                    .setNegativeButton((getString(android.R.string.no)), null)
                    .show();
        }
    }

    private void updateHomework() {
        // Remove old content
        hwArray.clear();
        final SourceHw s = new SourceHw(this);

        // Get content from SQLite Database
        try {
            s.open();
            hwArray = s.get(this);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Homework List", ex.toString());
        }

        final ListAdapter hw = CustomAdapter.entry(this, hwArray, true); //true means homework
        hwList.setAdapter(hw);
        registerForContextMenu(hwList);
    }

    private void updateExams() {
        // Remove old content
        exArray.clear();
        final SourceEx s = new SourceEx(this);

        // Get content from SQLite Database
        try {
            s.open();
            exArray = s.get(this);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Homework List", ex.toString());
        }

        final ListAdapter ex = CustomAdapter.entry(this, exArray, false); //false means exam
        exList.setAdapter(ex);
        registerForContextMenu(exList);
    }

    @Override
    public final void onResume() {
        super.onResume();
        updateHomework();
        updateExams();
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
                                updateHomework();
                            }
                        })
                .setNegativeButton((getString(android.R.string.no)), null)
                .show();
    }
}
