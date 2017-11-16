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

import com.ronschka.david.esb.AddHomework;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.databaseHomework.SourceHw;
import com.ronschka.david.esb.helper.Converter;
import com.ronschka.david.esb.helper.CustomAdapter;
import com.ronschka.david.esb.helper.Homework;
import com.ronschka.david.esb.helper.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeworkClass {
    final private Context context;
    final private TextView txtNoHomework;
    final private ListView hwList;
    private static ArrayList<HashMap<String, String>> hwArray = new ArrayList<>();

    public HomeworkClass(Context context, TextView txtNoHomework, ListView hwList){
        this.context = context;
        this.txtNoHomework = txtNoHomework;
        this.hwList = hwList;
    }

    public void createHomework(int positionSpinner){
        final SourceHw s = new SourceHw(context);

        // Get content from SQLite Database
        try {
            s.open();
            hwArray = s.get(context, positionSpinner);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Homework List", ex.toString());
        }
    }

    public void updateHomework(final int positionSpinner) {
        // Remove old content
        hwArray.clear();

        final SourceHw s = new SourceHw(context);

        // Get content from SQLite Database
        try {
            s.open();
            hwArray = s.get(context, positionSpinner);
            s.close();
        } catch (Exception ex) {
            Log.e("Update Homework List", ex.toString());
        }

        final ListAdapter hw = CustomAdapter.entry(context, hwArray, true); //true means homework
        ViewGroup.LayoutParams params = hwList.getLayoutParams();
        float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (hw.getCount() * 75.2 * scale + 0.5f);
        params.height = pixels;
        hwList.setFocusable(false);
        hwList.setLayoutParams(params);
        hwList.setAdapter(hw);
        ((Activity)context).registerForContextMenu(hwList); //important for creating the context menu

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        hwList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    if(positionSpinner == 0) {
                                        homeworkCompleted(hwArray, position, true); //true means completed
                                    }
                                    else{
                                        homeworkCompleted(hwArray, position, false);
                                    }
                                    updateHomework(positionSpinner);
                                }


                            }
                        });
        hwList.setOnTouchListener(touchListener);

        if(hw.getCount() == 0){ //list is empty
            txtNoHomework.setVisibility(View.VISIBLE);
        }
        else{
            txtNoHomework.setVisibility(View.GONE);
        }
    }

    private void homeworkCompleted(final ArrayList<HashMap<String, String>> list, int position, boolean completedBool){
        final String currentID = "ID = " + list.get(position).get(SourceHw.allColumns[0]);
        final String title = list.get(position).get(SourceHw.allColumns[1]);
        final String subject = list.get(position).get(SourceHw.allColumns[2]);
        final long time = Long.valueOf(list.get(position).get(SourceHw.allColumns[5])).longValue();
        final String info = list.get(position).get(SourceHw.allColumns[3]);
        final String urgent = list.get(position).get(SourceHw.allColumns[4]);
        final String color = list.get(position).get(SourceHw.allColumns[6]);
        final String completed;
        if(completedBool){
            completed = "true";
        }
        else{
            completed = "false";
        }
        Log.d("ESBLOG", "TEST: " + currentID + " " + title  + " " + subject + " " + time + " " + info + " " + urgent + " " + color + " " + completed);
        Homework.add(context, currentID, title, subject, time, info, urgent, color, completed);
    }

    public void editOne(final int pos) {
        final Intent intent;
        final Bundle mBundle;
        final String currentID = "ID = " + hwArray.get(pos).get(SourceHw.allColumns[0]);
        intent = new Intent(context, AddHomework.class);
        mBundle = new Bundle();
        mBundle.putString(SourceHw.allColumns[0], currentID);
        for (int i = 1; i < SourceHw.allColumns.length; i++)
            mBundle.putString(SourceHw.allColumns[i],
                    hwArray.get(pos).get(SourceHw.allColumns[i]));

        intent.putExtras(mBundle);
        context.startActivity(intent);
    }

    public void deleteOne(final int pos, final int spinnerPos) {
        final ArrayList<HashMap<String, String>> tempArray = Converter.toTmpArray(hwArray, pos);

        final String currentID = "ID = " + hwArray.get(pos).get(SourceHw.allColumns[0]);
        final SimpleAdapter alertAdapter = CustomAdapter.entry(context, tempArray, true);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setTitle(R.string.delete_homework)
                .setAdapter(alertAdapter, null)
                .setPositiveButton((context.getString(android.R.string.yes)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(final DialogInterface d, final int i) {
                                Homework.delete(context, currentID);
                                updateHomework(spinnerPos);
                            }
                        })
                .setNegativeButton((context.getString(android.R.string.no)), null)
                .show();
        }
}
