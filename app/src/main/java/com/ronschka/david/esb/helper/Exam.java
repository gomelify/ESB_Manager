package com.ronschka.david.esb.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ronschka.david.esb.R;
import com.ronschka.david.esb.databaseExams.HelperEx;
import com.ronschka.david.esb.databaseExams.SourceEx;
import com.ronschka.david.esb.databaseHomework.SourceHw;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Exam {

    /**
     * Deletes one homework.
     *
     * @param c  Needed by {@link SourceHw}.
     * @param ID ID of homework to get deleted. If set to null, all homework will get deleted.
     */
    public static void delete(final Context c, final String ID) {
        final SourceHw s = new SourceHw(c);
        s.delete_item(ID);
    }

    /**
     * Deletes multiple homework.
     *
     * @param c  Needed by {@link SourceHw}.
     * @param IDs IDs of homework to get deleted. If set to null, all homework will get deleted.
     */
    public static void delete(final String[] IDs, final Context c) {
        for (String ID : IDs) {
            delete(c, ID);
        }
    }

    /**
     * Adds a homework.
     *
     * @param c         Needed by {@link SourceHw}.
     * @param ID        The ID used in the database.
     * @param title     The title of the homework.
     * @param subject   The subject of the homework.
     * @param time      The time until the homework has to be done.
     * @param info      Additional information to the homework.
     * @param urgent    Is it urgent?
     * @param color     Color of the rectangle
     * @param completed Is it completed?
     */
    public static void add(final Context c, final String ID, final String title,
                           final String subject, final long time, final String info,
                           final String urgent, final String color, final String completed) {
        try {
            final SourceEx s = new SourceEx(c);
            s.open();
            s.createEntry(c, ID, title, subject, time, info, urgent, color, completed);
            s.close();
        } catch (final Exception ex) {
            Log.e("Database", ex.toString());
        }
    }
    /**
     * Exports the homework database.
     *
     * @param c    Needed by {@link Utils}.
     * @param auto Indicates if it's an automatic backup.
     */
    public static void exportIt(final Context c, final boolean auto) {
        // Check if directory exists
        final File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name));
        if (!(dir.exists()))
            dir.mkdir();

        String stamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.US).format(new Date());

        if (auto)
            stamp = "auto-backup";

        // Path for Database
        final File srcDB = new File(c.getApplicationInfo().dataDir
                + "/databases/" + HelperEx.DATABASE_NAME);
        final File dstDB = new File(Environment.getExternalStorageDirectory() + "/"
                + c.getString(R.string.app_name) + "/Exam-" + stamp + ".db");

        if (dstDB.exists())
            dstDB.delete();
    }
}
