package com.ronschka.david.esb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PhpClass extends AsyncTask<Void, Void, Void> {

    //Do this, if SettingsActivity is created
    private Context mContext;

    //important for preferences!
    public void setContext(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            URL url = new URL("http://www.esb-hamm.de/app/kuerzel_vertretungsplan_schueler.php");
            InputStream in = url.openStream();

            Document list = Jsoup.parse(in,"ISO-8859-1", "http://www.esb-hamm.de/app/"); //iso to show special chars "ä,ü,ö.."
            String classList = list.text();

            //Test classes for internal tests
            //classList = "\"ARE\",\"BAR\",\"BEC\",\"BEI\",\"BEK\",\"BEM\",\"BOI\",\"BOR\",\"BRE\",\"BRO\",\"BRS\",\"DÜT\",\"ENG\",\"FEI\",\"FEL\",\"FIR\",\"FRA\",\"FRE\",\"GOR\",\"GRI\",\"HAS\",\"HEB\",\"HEI\",\"HEL\",\"HOB\",\"HOE\",\"JOC\",\"KEL\",\"KLE\",\"KLS\",\"KRE\",\"KUN\",\"LAM\",\"LAN\",\"LHS\",\"LIB\",\"LIE\",\"LOH\",\"MAH\",\"MAI\",\"MEI\",\"MEN\",\"MIS\",\"MUE\",\"MVB\",\"NIE\",\"NIM\",\"OTT\",\"PHI\",\"REC\",\"REM\",\"RIE\",\"RÖL\",\"ROS\",\"RUD\",\"RUE\",\"SAS\",\"SCA\",\"SCM\",\"SCN\",\"SCU\",\"SHI\",\"SPY\",\"SRG\",\"STA\",\"STE\",\"STO\",\"SWG\",\"THO\",\"VAU\",\"VIT\",\"VOG\",\"WAL\",\"WED\",\"WEI\",\"WET\",\"WIN\",\"WIT\",\"ZMU\"SPLIT\"Test1\",\"Test2\",\"Test3\",\"Test4\",\"Test5\",\"Test6\",\"Test7\",\"Test8\",\"Test9\",\"Test10\",\"Test11\",\"Test12\",\"Test13\",\"Test14\",\"Test15\",\"Test16\",\"Test17\",\"Test18\",\"GO12\",\"Null\"SPLIT\"E003\",\"E004\",\"E102\",\"E103\",\"E104\",\"E105\",\"E108\",\"E109\",\"E202\",\"E203\",\"E204\",\"E205\",\"E206\",\"E208\",\"E209\",\"E212\",\"E302\",\"E304\",\"E305\",\"E306\",\"E307\",\"E308\",\"E310\",\"EWST1\",\"EWST2\",\"H003\",\"H004\",\"H005\",\"H008\",\"H009\",\"H102\",\"H106\",\"H108\",\"H111\",\"H113\",\"H202\",\"H206a\",\"H207\",\"H208\",\"H210\",\"H212\",\"H214\",\"H215\",\"H217\",\"H300\",\"H301\",\"H304\",\"H304a\",\"H305\",\"H306\",\"H309\",\"H310\",\"H311\",\"H312\",\"H315\",\"H317\",\"H320\",\"H322\",\"HWST\",\"MWST1\",\"MWST2\",\"T1\",\"T2\",\"T3\",\"V208\",\"V209\",\"W005\",\"W101\",\"W102\",\"W201\",\"W202\",\"Pau\"";

            //edit the output
            classList = classList.replace('"',' ');
            classList = classList.replaceAll(" ","");

            //set phpData from Settings to received php String
            SettingsActivity.phpData = classList;

            //Store the phpData in an Preference -> is used in SettingsActivity
            SharedPreferences classListStorage = mContext.getSharedPreferences("phpData", 0);
            SharedPreferences.Editor editor = classListStorage.edit();
            editor.putString("PHP", classList);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
