package com.ronschka.david.esb.helper;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.ronschka.david.esb.R;
import com.ronschka.david.esb.databaseHomework.SourceHw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public final class Utils {

    private static boolean isActionBarAvailable = false;

    /**
     * A fix for a VerifyError crash on old versions
     * of Android
     */
    static {
        try {
            ActionBarWrapper.isAvailable();
            isActionBarAvailable = true;
        } catch (Throwable t) {
            isActionBarAvailable = false;
        }
    }

    public static void setupActionBar(final Context context, final boolean isPreferenceActivity) {
        if (Build.VERSION.SDK_INT >= 11 && isActionBarAvailable) {
            final ActionBarWrapper actionBarWrapper = new ActionBarWrapper(context, isPreferenceActivity);
            actionBarWrapper.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Cross out solved homework.
     *
     * @param e       {@link android.widget.ExpandableListAdapter} which contains the homework.
     * @param hwArray {@link java.util.ArrayList} which contains the homework.
     */
    public static void crossOut(final ExpandableListAdapter e, final ArrayList<HashMap<String, String>> hwArray) {
        for (int i = 0; i < hwArray.size(); i++) {
            if (!hwArray.get(i).get(SourceHw.allColumns[6]).equals("")) {
                final View v = e.getGroupView(i, false, null, null);

                final TextView tv1 = v.findViewById(R.id.textView_subject);
                final TextView tv2 = v.findViewById(R.id.textView_until);
                final TextView tv3 = v.findViewById(R.id.textView_homework);
                final TextView tv4 = v.findViewById(R.id.textView_urgent);
                tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv2.setPaintFlags(tv2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv3.setPaintFlags(tv3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv4.setPaintFlags(tv4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    /**
     * Transfers a file.
     *
     * @param src SourceEx from where the file has to be transferred.
     * @param dst SourceEx to where the file has to be transferred.
     */
    public static boolean transfer(final File src, final File dst) {
        try {
            final FileInputStream inStream = new FileInputStream(src);
            final FileOutputStream outStream = new FileOutputStream(dst);
            final FileChannel inChannel = inStream.getChannel();
            final FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return true;
        } catch (final FileNotFoundException e) {
            Log.e("FileNotFoundException", e.toString());
            return false;
        } catch (final IOException e) {
            Log.e("IOException", e.toString());
            return false;
        }
    }
}



