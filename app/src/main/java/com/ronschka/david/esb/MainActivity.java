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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ronschka.david.esb.databaseExams.SourceEx;
import com.ronschka.david.esb.databaseHomework.SourceHw;
import com.ronschka.david.esb.helper.Converter;
import com.ronschka.david.esb.helper.CustomAdapter;
import com.ronschka.david.esb.helper.Exam;
import com.ronschka.david.esb.helper.Homework;
import com.ronschka.david.esb.tabs.SubstitutionClass;
import com.ronschka.david.esb.tabs.TimetableClass;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private static ArrayList<HashMap<String, String>> hwArray = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> exArray = new ArrayList<>();
    private int previousChild; //previousChild view
    private SwipeRefreshLayout swipeContainerSub, swipeContainerTime;
    private ProgressBar progressBar;
    private ViewFlipper viewFlipper;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView substitutionRecycler, timetableRecycler;
    private ListView hwList;
    private ListView exList;

    //tabs
    private SubstitutionClass substitutionClass;
    private TimetableClass timetableClass;

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
            //view changer
            viewFlipper = findViewById(R.id.viewFlipper);

            //bottom navigation interface
            bottomNavigationView = findViewById(R.id.navigation);

            //fab -> homework and exam
            fab = findViewById(R.id.fabButton);

            if (savedInstanceState != null) {
                int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
                int navigationPosition = savedInstanceState.getInt("NAVIGATION_NUMBER");
                viewFlipper.setDisplayedChild(flipperPosition);
                bottomNavigationView.setSelectedItemId(navigationPosition);
                previousChild = savedInstanceState.getInt("TAB_NUMBER");

                if(navigationPosition == R.id.navigation_homework || navigationPosition == R.id.navigation_exam){
                    fab.setVisibility(View.VISIBLE);
                }
            }
            else{
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

        //set viewFlipper's animations
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.view_flipper));

        //SwipeRefresher -> substitution
        swipeContainerSub = findViewById(R.id.swipeContainerSub);
        // Configure the refreshing colors
        swipeContainerSub.setColorSchemeResources(R.color.colorTextAccent);
        swipeContainerSub.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createSubstitutionView();
            }
        });

        //SwipeRefresher -> timetable
        swipeContainerTime = findViewById(R.id.swipeContainerTime);
        //Configure the refreshing colors
        swipeContainerTime.setColorSchemeResources(R.color.colorTextAccent);
        swipeContainerTime.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createTimetableView();
            }
        });

        progressBar = findViewById(R.id.progressBar);

        //homework list
        hwList = findViewById(R.id.listViewHomework);
        exList = findViewById(R.id.listViewExams);

        //RecyclerView -> substitution
        substitutionRecycler = findViewById(R.id.substitutionRecycler);
        substitutionRecycler.setItemViewCacheSize(30);
        substitutionRecycler.setDrawingCacheEnabled(true);
        substitutionRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        substitutionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //RecyclerView -> timetable
        timetableRecycler = findViewById(R.id.timetableRecycler);

        //spacing between the cardViews in timetable in dp
        final int spacingDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        //needed for timetable (proportions)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int cardWidth = (width - 10*spacingDP)/5; //cardWidth in timetableClass

        //initialize the tab classes
        substitutionClass = new SubstitutionClass(getApplicationContext(), MainActivity.this, substitutionRecycler);
        timetableClass = new TimetableClass(getApplicationContext(), timetableRecycler, spacingDP, cardWidth);

        //Animation
        final Animation fab_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_up);
        final Animation fab_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_down);

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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            //switch views
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (previousChild != item.getItemId()) {
                    switch ((item.getItemId())) {
                        case R.id.navigation_plan:
                            if(previousChild == R.id.navigation_homework || previousChild == R.id.navigation_exam){
                                fab.startAnimation(fab_hide);
                            }
                            fab.setVisibility(View.GONE);
                            previousChild = R.id.navigation_plan;
                            viewFlipper.setDisplayedChild(0);
                            break;
                        case R.id.navigation_substitution:
                            if(previousChild == R.id.navigation_homework || previousChild == R.id.navigation_exam){
                                fab.startAnimation(fab_hide);
                            }
                            fab.setVisibility(View.GONE);
                            viewFlipper.setDisplayedChild(1);
                            previousChild = R.id.navigation_substitution;
                            break;
                        case R.id.navigation_homework:
                            fab.setVisibility(View.VISIBLE);
                            if(!(previousChild == R.id.navigation_exam)){
                                fab.startAnimation(fab_show);
                            }
                            viewFlipper.setDisplayedChild(2);
                            previousChild = R.id.navigation_homework;
                            break;
                        case R.id.navigation_exam:
                            fab.setVisibility(View.VISIBLE);
                            if(!(previousChild == R.id.navigation_homework)){
                                fab.startAnimation(fab_show);
                            }
                            viewFlipper.setDisplayedChild(3);
                            previousChild = R.id.navigation_exam;
                            break;
                    }
                }
                else{
                    //scrolls up if selected navigation become touched again
                    switch (previousChild) {
                        case R.id.navigation_plan:
                            break;
                        case R.id.navigation_substitution:
                            substitutionRecycler.smoothScrollToPosition(0);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        int position = viewFlipper.getDisplayedChild();
        int navigation = bottomNavigationView.getSelectedItemId();
        savedInstanceState.putInt("TAB_NUMBER", position);
        savedInstanceState.putInt("NAVIGATION_NUMBER", navigation);
    }

    private void createSubstitutionView(){
        if(!(swipeContainerSub.isRefreshing())){
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

    private void createTimetableView(){
        final int spacingDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int cardWidth = (width - 10*spacingDP)/5; //cardWidth in timetableClass

        timetableClass.rebuild(cardWidth);
        swipeContainerTime.setRefreshing(false);
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
        swipeContainerSub.setRefreshing(false);
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
        final Intent intent;
        final Bundle mBundle;
        if(type) {
            final String currentID = "ID = " + Array.get(pos).get(SourceHw.allColumns[0]);
            intent = new Intent(this, AddHomework.class);
            mBundle = new Bundle();
            mBundle.putString(SourceHw.allColumns[0], currentID);
            for (int i = 1; i < SourceHw.allColumns.length; i++)
                mBundle.putString(SourceHw.allColumns[i],
                        Array.get(pos).get(SourceHw.allColumns[i]));
        }
        else{
            final String currentID = "ID = " + Array.get(pos).get(SourceEx.allColumns[0]);
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
            final SimpleAdapter alertAdapter = CustomAdapter.entry(this, tempArray, false);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog
                    .setTitle(R.string.delete_exam)
                    .setAdapter(alertAdapter, null)
                    .setPositiveButton((getString(android.R.string.yes)),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(final DialogInterface d, final int i) {
                                    Exam.delete(getApplicationContext(), currentID);
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

        TextView txt = findViewById(R.id.txtNoHomework);

        if(hw.getCount() == 0){ //list is empty
            txt.setVisibility(View.VISIBLE);
        }
        else{
            txt.setVisibility(View.GONE);
        }
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
        Log.d("ESBLOG", "Adapter: " + ex.getCount());
        exList.setAdapter(ex);
        registerForContextMenu(exList);

        TextView txt = findViewById(R.id.txtNoExams);

        if(ex.getCount() == 0){ //list is empty
            txt.setVisibility(View.VISIBLE);
        }
        else{
            txt.setVisibility(View.GONE);
        }
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
