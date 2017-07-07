package com.ronschka.david.esb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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

        //WebView
        final WebView web = (WebView) findViewById(R.id.web_view);
        web.setWebViewClient(new MyWebViewClient());
        web.getSettings().setJavaScriptEnabled(true); //For selection of elements
        web.loadUrl("http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/default.htm?art=w&name=GO12");

        //Animations
        final Animation fab_sync = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_sync);

        //FloatingButton
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //CalenderButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUrl();
                fab.startAnimation(fab_sync);
                web.loadUrl("http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/default.htm?art=w&name=GO12");
            }
        });

        parseUrl();
    }
    private class MyWebViewClient extends WebViewClient{

        //Progress loading function
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(request.toString());
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }


        //Authentication function
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                                              HttpAuthHandler handler, String host, String realm){

            //IMPORTANT ADD: The page saves cookies for AuthRequests automatically, so one
            //successful Login let you logged in for a while.
            //-add Cookie Remover if User Logout

            //get Userdata of LoginPreference
            SharedPreferences login = getSharedPreferences("Login", 0);
            String user = login.getString("Unm","");
            String pass = login.getString("Psw","");

            //Proceed Userdata to HttpRequest
            handler.proceed(user, pass);

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

                    //Loads the page with the user and password input
                    WebView web = (WebView) findViewById(R.id.web_view);
                    web.setWebViewClient(new MyWebViewClient());
                    web.getSettings().setJavaScriptEnabled(true); //Für Klassenauswahl

                    web.loadUrl("http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/default.htm?art=w&name=GO12");
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

    public void parseUrl(){
        //create the PrefsFragment in SettingsActivity to get a
        //PreferenceFragment and read the value of list
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String classNumber = pref.getString("classList","0");
        String attach;

        //system expects format like '00', '01', .. '99' etc.
        if(classNumber.length() < 2){
            attach = "0" + classNumber;
        }
        else{
            attach = classNumber;
        }

        //this URL with the class attachment will be parsed
        String url = "http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/w/27/w000" + attach + ".htm";

        //get Userdata of LoginPreference
        SharedPreferences login = getSharedPreferences("Login", 0);
        String user = login.getString("Unm","");
        String pass = login.getString("Psw","");

        //start parser with following parameter
        new ParserClass().execute(url, user, pass);
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
                WebView web = (WebView)findViewById(R.id.web_view);
                web.loadUrl("http://www.esb-hamm.de/vertretungsplan/vplan/klassen/vplanklassenuntis/default.htm?art=w&name=" + query);
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
            startActivity(settings);
            return true;
        }
        else if (id == R.id.action_login) {
            Intent test = new Intent(getApplicationContext(), CardViewClass.class);
            startActivity(test);

            //to test the new view
            //setContentView(R.layout.vplan_main);

            //onCreateLoginWindow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
