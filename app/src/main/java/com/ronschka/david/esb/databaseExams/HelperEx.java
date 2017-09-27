package com.ronschka.david.esb.databaseExams;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class HelperEx extends SQLiteOpenHelper {

    /**
     * The name of the database containing the exam.
     */
    public static final String DATABASE_NAME = "Exam.db";

    /**
     * The current version of the database containing the exam.
     */
    private static final int DATABASE_VERSION = 3;

    /**
     * The command when first creating the database.
     */
    private static final String TABLE_CREATE_EXAM = "create table EXAM(ID integer primary key autoincrement,HOMEWORK text,SUBJECT text,TIME text,INFO text,URGENT text,COLOR text,COMPLETED text)";

    public HelperEx(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public final void onCreate(final SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE_EXAM);
    }

    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // Upgrade from first to third version
        if (oldVersion == 1 && newVersion == 3) {
            db.execSQL("ALTER TABLE EXAM ADD COLUMN INFO TEXT");
            db.execSQL("ALTER TABLE EXAM ADD COLUMN TIME TEXT");
            db.execSQL("ALTER TABLE EXAM ADD COLUMN COMPLETED TEXT");
            Log.w(HelperEx.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ".");
        }
        // Upgrade from second to third version
        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL("ALTER TABLE EXAM ADD COLUMN COMPLETED TEXT");
            Log.w(HelperEx.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ".");
        }
    }
}
