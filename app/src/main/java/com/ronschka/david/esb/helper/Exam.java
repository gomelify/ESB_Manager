package com.ronschka.david.esb.helper;

import android.content.Context;
import android.util.Log;

import com.ronschka.david.esb.databaseExams.SourceEx;
import com.ronschka.david.esb.databaseHomework.SourceHw;

public final class Exam {

    /**
     * Deletes one homework.
     *
     * @param c  Needed by {@link SourceHw}.
     * @param ID ID of homework to get deleted. If set to null, all homework will get deleted.
     */
    public static void delete(final Context c, final String ID) {
        final SourceEx s = new SourceEx(c);
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
     */
    public static void add(final Context c, final String ID, final String title,
                           final String subject, final long time, final String info,
                           final String urgent, final String color) {
        try {
            final SourceEx s = new SourceEx(c);
            s.open();
            s.createEntry(c, ID, title, subject, time, info, urgent, color);
            s.close();
        } catch (final Exception ex) {
            Log.e("Database", ex.toString());
        }
    }
}
