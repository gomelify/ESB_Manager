package com.ronschka.david.esb.helper;

import com.ronschka.david.esb.databaseHomework.SourceHw;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public final class Converter {

    /**
     * Converts an ArrayList with multiples HashMaps to an ArrayList with just one HashMap.
     *
     * @param ArHa An ArrayList with multiples HashMaps.
     * @param pos  Indicates which HashMap has to be used.
     */
    public static ArrayList<HashMap<String, String>> toTmpArray(final ArrayList<HashMap<String, String>> ArHa, final int pos) {
        // Temporary ArrayList containing a HashMap
        final ArrayList<HashMap<String, String>> tempArHa = new ArrayList<>();

        // Temporary HashMap
        final HashMap<String, String> tempHashMap = new HashMap<>();

        // Fill temporary HashMap with one row of original HashMap
        for (int i = 0; i < SourceHw.allColumns.length; i++)
            tempHashMap.put(SourceHw.allColumns[i],
                    ArHa.get(pos).get(SourceHw.allColumns[i]));

        final String date = toDate(Long.valueOf(ArHa.get(pos).get(SourceHw.allColumns[5])).longValue());
        tempHashMap.put("UNTIL", date);

        // Add temporary HashMap to temporary ArrayList containing a HashMap
        tempArHa.add(tempHashMap);
        return tempArHa;
    }

    /**
     * Converts a time in milliseconds to a date.
     *
     * @param time Time in milliseconds,
     */
    public static String toDate(final long time) {
        String until;
        // Format to 31.12.14 or local version of that
        final DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault());
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time);

        // Format to Week of Day, for example Mo. or local version of that
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",
                Locale.getDefault());

        // Tab space because else the date is too far to the left
        until = (dateFormat.format(gc.getTime()) + ", " + f.format(gc.getTime()));
        return until;
    }

    /**
     * Converts a time in an int array to a date.
     *
     * @param time Time in an int array,
     */
    public static String toDate(final int[] time) {
        String until;
        // Format to 31.12.14 or local version of that
        final DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault());
        final GregorianCalendar gc = new GregorianCalendar(time[0], time[1], time[2]);

        // Format to Week of Day, for example Mo. or local version of that
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE",
                Locale.getDefault());

        // Tab space because else the date is too far to the left
        until = (dateFormat.format(gc.getTime()) + ", " + f.format(gc.getTime()));
        return until;
    }

    /**
     * Converts a time in milliseconds to the time in milliseconds.
     *
     * @param time Time in an int array to a date.
     */
    public static long toMilliseconds(final int[] time) {
        final GregorianCalendar gc = new GregorianCalendar(time[0], time[1], time[2]);
        return gc.getTimeInMillis();
    }
}
