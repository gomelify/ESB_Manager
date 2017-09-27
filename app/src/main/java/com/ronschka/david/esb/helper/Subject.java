package com.ronschka.david.esb.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;

public final class Subject {

    /**
     * Default {@link android.content.SharedPreferences} used in this class.
     */
    private static SharedPreferences prefs;

    /**
     * Initializes the default {@link android.content.SharedPreferences} used in this class.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    private static void initPrefs(final Context c) {
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
    }

    /**
     * Returns a list with all subjects used by the user.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public static String[] get(final Context c) {
        initPrefs(c);

        // Set size of array to amount of Strings in SharedPreferences
        final int size = prefs.getInt("subjects_size", 10);
        final String[] subjects = new String[size];

        // Get parts of subject array from SharedPreferences Strings
        for (int i = 0; i < size; i++)
           // subjects[i] = prefs.getString("subjects_" + i, "Test");
             switch (i){
                 case 0:
                     subjects[i] = "Mathe";
                     break;
                 case 1:
                     subjects[i] = "Deutsch";
                     break;
                 case 2:
                     subjects[i] = "Englisch";
                     break;
                 case 3:
                     subjects[i] = "Informatik";
                     break;
                 case 4:
                     subjects[i] = "Elektrotechnik";
                     break;
                 case 5:
                     subjects[i] = "Physik";
                     break;
                 case 6:
                     subjects[i] = "Wirtschaft";
                     break;
                 case 7:
                     subjects[i] = "Technische Informatik";
                     break;
                 case 8:
                     subjects[i] = "Gesellschaftslehre";
                     break;
                 case 9:
                     subjects[i] = "Religion";
                     break;
             }

        return subjects;
    }

    /**
     * Adds a subject.
     *
     * @param c       Needed by {@link android.preference.PreferenceManager}.
     * @param subject The subject to add.
     */
    public static void add(final Context c, final String subject) {
        initPrefs(c);
        final int size = prefs.getInt("subjects_size", 0);
        final String[] subjects = new String[size + 1];

        for (int i = 0; i < size; i++)
            subjects[i] = prefs.getString("subjects_" + i, null);

        subjects[size] = subject;

        final SharedPreferences.Editor editor = prefs.edit();
        Arrays.sort(subjects);

        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.putInt("subjects_size", subjects.length);
        editor.commit();

        final String sAdded = "Added";
    }

    /**
     * Resets the list of subjects.
     *
     * @param c Needed by {@link android.preference.PreferenceManager}.
     */
    public static void setDefault(final Context c) {
        // Get subjects from strings.xml
        final String[] subjects = {"test1", "test2", "test3"};

        // Sort subjects array alphabetically
        Arrays.sort(subjects);

        // Add subjects to SharedPreferences
        initPrefs(c);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("subjects_size", subjects.length);
        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.commit();
    }

    /**
     * Deletes a subject.
     *
     * @param c   Needed by {@link android.preference.PreferenceManager}.
     * @param pos The subject to delete.
     */
    public static void delete(final Context c, final int pos) {
        initPrefs(c);
        final int size = prefs.getInt("subjects_size", 0);
        final String[] subjects = new String[size - 1];

        for (int i = 0; i < size; i++) {
            if (i < pos)
                subjects[i] = prefs.getString("subjects_" + i, null);

            if (i > pos)
                subjects[i - 1] = prefs.getString("subjects_" + i, null);
        }

        final SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < subjects.length; i++)
            editor.putString("subjects_" + i, subjects[i]);

        editor.putInt("subjects_size", subjects.length);
        editor.commit();
    }
}
