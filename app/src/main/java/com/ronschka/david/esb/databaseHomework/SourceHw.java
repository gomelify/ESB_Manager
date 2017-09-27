package com.ronschka.david.esb.databaseHomework;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.ronschka.david.esb.helper.Converter;
import com.ronschka.david.esb.helper.Homework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public final class SourceHw {

    /**
     * All columns used in the database.
     */
    public static final String[] allColumns = {"ID", "HOMEWORK", "SUBJECT", "INFO", "URGENT", "TIME", "COMPLETED", "COLOR"};

    /**
     * The {@link HelperHw} used in this class.
     */
    private final HelperHw dbHelper;

    /**
     * The {@link android.database.sqlite.SQLiteDatabase} used in this class.
     */
    private SQLiteDatabase database;

    /**
     * Initializes the {@link HelperHw} used in this class.
     *
     * @param context Needed by {@link HelperHw}.
     */
    public SourceHw(final Context context) {
        dbHelper = new HelperHw(context);
    }

    /**
     * Opens the {@link HelperHw} used in this class.
     */
    public final void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the {@link HelperHw} used in this class.
     */
    public final void close() {
        dbHelper.close();
    }

    /**
     * Adds a homework.
     *
     * @param c         Needed by {@link HelperHw}.
     * @param ID        The ID used in the database.
     * @param title     The title of the homework.
     * @param subject   The subject of the homework.
     * @param time      The time until the homework has to be done.
     * @param info      Additional information to the homework.
     * @param urgent    Is it urgent?
     * @param color     Color of the rectangle
     * @param completed Is it completed?
     */
    public final void createEntry(final Context c, final String ID, final String title,
                                  final String subject, final long time, final String info,
                                  final String urgent, final String color, final String completed) {
        final ContentValues values = new ContentValues();
        values.put(allColumns[1], title);
        values.put(allColumns[2], subject);
        values.put(allColumns[3], info);
        values.put(allColumns[4], urgent);
        values.put(allColumns[5], String.valueOf(time));
        values.put(allColumns[6], color);
        values.put(allColumns[7], completed);

        String insertId = "ID = " + database.insert("HOMEWORK", null, values);
        if (ID != null) {
            Homework.delete(c, ID);
            insertId = ID;
        }

        final Cursor cursor = database.query("HOMEWORK", allColumns, insertId, null,
                null, null, null);
        cursor.close();
        cursor.moveToFirst();
    }

    /**
     * Deletes an item in the database.
     *
     * @param where Row to delete.
     */
    public final void delete_item(final String where) {
        open();
        database.delete("HOMEWORK", where, null);
        close();
    }

    /**
     * Returns an ArrayList containing HashMaps containing the homework.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public final ArrayList<HashMap<String, String>> get(final Context c) {
        final ArrayList<HashMap<String, String>> entriesList = new ArrayList<>();

        final Cursor cursor = database.query("HOMEWORK", allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0)
            return entriesList;

        while (!cursor.isAfterLast()) {
            final HashMap<String, String> temp = new HashMap<>();
            temp.put(allColumns[0], String.valueOf(cursor.getLong(0)));
            for (int i = 1; i < allColumns.length; i++) {
                // Support upgrades from older versions
                if (i == 5 && cursor.getString(i) == null)
                    temp.put(allColumns[i], "1420066800000");
                else
                    temp.put(allColumns[i], cursor.getString(i));
                if (i == 7 && cursor.getString(i) == null)
                    temp.put(allColumns[i], "");
                else
                    temp.put(allColumns[i], cursor.getString(i));
            }
            entriesList.add(temp);
            cursor.moveToNext();
        }
        cursor.close();

        // Sort by time
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if (prefs.getBoolean("pref_sortbytime", true))
            Collections.sort(entriesList, new Comparator<HashMap<String, String>>() {

                @Override
                public final int compare(final HashMap<String, String> lhs, final HashMap<String, String> rhs) {
                    final long a = Long.valueOf(lhs.get(allColumns[5])).longValue();
                    final long b = Long.valueOf(rhs.get(allColumns[5])).longValue();
                    return Long.valueOf(a).compareTo(Long.valueOf(b));
                }
            });

        // Sort by importance
        if (prefs.getBoolean("pref_sortbyimportance", false))
            Collections.sort(entriesList, new Comparator<HashMap<String, String>>() {

                @Override
                public final int compare(final HashMap<String, String> lhs, final HashMap<String, String> rhs) {
                    final String a = rhs.get(allColumns[4]);
                    final String b = lhs.get(allColumns[4]);
                    return a.compareTo(b);
                }
            });

        // Add date, based on time in milliseconds
        for (int i = 0; i < entriesList.size(); i++) {
            final HashMap<String, String> temp = entriesList.get(i);
            final long time = Long.valueOf(temp.get(allColumns[5])).longValue();
            final String date = Converter.toDate(time);
            temp.put("UNTIL", date);
        }
        return entriesList;
    }
}