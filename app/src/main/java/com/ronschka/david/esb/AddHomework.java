package com.ronschka.david.esb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public final class AddHomework extends AppCompatActivity{

    private static long time;

    private static String[] subjects;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_add);
    }

    private int[] getDate(final long time) {
        final Calendar c = Calendar.getInstance();
        if (time != 0)
            c.setTimeInMillis(time);

        final int[] tmpDate = new int[3];

        // E.g "1970"
        tmpDate[0] = c.get(Calendar.YEAR);

        // E.g "01"
        tmpDate[1] = c.get(Calendar.MONTH);

        // Get current day, e.g. "01", plus one day > e.g. "02"
        tmpDate[2] = c.get(Calendar.DAY_OF_MONTH) + 1;

        if (time != 0)
            tmpDate[2] = c.get(Calendar.DAY_OF_MONTH);

        return tmpDate;
    }

    private void setUntilTV(final int[] date) {
        //final String until = toDate(date);
        final TextView untilTV = (TextView) findViewById(R.id.button_until);
        //untilTV.setText(until);
        //time =
    }

    private void setSpinner() {
        final Spinner subSpin = (Spinner) findViewById(R.id.spinner_subject);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subSpin.setAdapter(adapter);
    }

}


