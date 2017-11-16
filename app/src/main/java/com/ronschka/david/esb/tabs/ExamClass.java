package com.ronschka.david.esb.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ronschka.david.esb.AddExam;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.databaseExams.SourceEx;
import com.ronschka.david.esb.helper.Converter;
import com.ronschka.david.esb.helper.CustomAdapter;
import com.ronschka.david.esb.helper.Exam;

import java.util.ArrayList;
import java.util.HashMap;

public class ExamClass {
    final private Context context;
    final private TextView txtNoExam;
    final private ListView exList;
    private static ArrayList<HashMap<String, String>> exArray = new ArrayList<>();

    public ExamClass(Context context, TextView txtNoExam, ListView hwList){
        this.context = context;
        this.txtNoExam = txtNoExam;
        this.exList = hwList;
    }

    public void createExam(int positionSpinner){
        final SourceEx s = new SourceEx(context);

        // Get content from SQLite Database
        try {
            s.open();
            exArray = s.get(context, positionSpinner);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Exam List", ex.toString());
        }
    }

    public void updateExams(final int positionSpinner) {
        // Remove old content
        exArray.clear();

        final SourceEx s = new SourceEx(context);

        // Get content from SQLite Database
        try {
            s.open();
            exArray = s.get(context, positionSpinner);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Exam List", ex.toString());
        }

        final ListAdapter ex = CustomAdapter.entry(context, exArray, false); //false means exam
        ViewGroup.LayoutParams params = exList.getLayoutParams();
        float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (ex.getCount() * 74.7 * scale + 0.5f);
        params.height = pixels;
        exList.setFocusable(false);
        exList.setLayoutParams(params);
        exList.setAdapter(ex);
        ((Activity)context).registerForContextMenu(exList); //important for creating the context menu

        if(ex.getCount() == 0){ //list is empty
            txtNoExam.setVisibility(View.VISIBLE);
        }
        else{
            txtNoExam.setVisibility(View.GONE);
        }
    }

    public void editOne(final int pos) {
        final Intent intent;
        final Bundle mBundle;
        final String currentID = "ID = " + exArray.get(pos).get(SourceEx.allColumns[0]);
        intent = new Intent(context, AddExam.class);
        mBundle = new Bundle();
        mBundle.putString(SourceEx.allColumns[0], currentID);
        for (int i = 1; i < SourceEx.allColumns.length; i++)
            mBundle.putString(SourceEx.allColumns[i],
                    exArray.get(pos).get(SourceEx.allColumns[i]));

        intent.putExtras(mBundle);
        context.startActivity(intent);
    }

    public void deleteOne(final int pos, final int spinnerPos) {
        final ArrayList<HashMap<String, String>> tempArray = Converter.toTmpArray(exArray, pos);

        final String currentID = "ID = " + exArray.get(pos).get(SourceEx.allColumns[0]);
        final SimpleAdapter alertAdapter = CustomAdapter.entry(context, tempArray, false);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setTitle(R.string.delete_exam)
                .setAdapter(alertAdapter, null)
                .setPositiveButton((context.getString(android.R.string.yes)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface d, final int i) {
                                Exam.delete(context, currentID);
                                updateExams(spinnerPos);
                            }
                        })
                .setNegativeButton((context.getString(android.R.string.no)), null)
                .show();
    }
}
