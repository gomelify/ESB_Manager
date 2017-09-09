package com.ronschka.david.esb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

public class ColorListActivity extends AppCompatActivity{

    int actualPosition;
    String actualColorString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_colors);
        //back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Preference for color values
        final SharedPreferences colors = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String[] textString = {
                getString(R.string.substitution),
                getString(R.string.teacherChange),
                getString(R.string.event),
                getString(R.string.co_supervision),
                getString(R.string.roomChange),
                getString(R.string.message_of_the_day),
                getString(R.string.reset_colors)
        };

        int[] drawableIds = {
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
                R.drawable.ic_color_lens_black_24dp,
        };

        ListView listView = (ListView)findViewById(R.id.color_listview);

        final CustomAdapter arrayAdapter = new CustomAdapter(
                this,
                textString,
                drawableIds){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                ImageView img = (ImageView) view.findViewById(R.id.color_image);

                // Set the text color of image (ListView Item)
                switch(position){
                    case 0:
                        String colorCancel = colors.getString("color_cancel", getResources().getString(0+R.color.standardCancel));
                        img.setColorFilter(Color.parseColor(colorCancel));
                        break;
                    case 1:
                        String colorChange = colors.getString("color_change", getResources().getString(0+R.color.standardChange));
                        img.setColorFilter(Color.parseColor(colorChange));
                        break;
                    case 2:
                        String colorEvent = colors.getString("color_event", getResources().getString(0+R.color.standardEvent));
                        img.setColorFilter(Color.parseColor(colorEvent));
                        break;
                    case 3:
                        String colorWithOther = colors.getString("color_with_other", getResources().getString(0+R.color.standardWithOther));
                        img.setColorFilter(Color.parseColor(colorWithOther));
                        break;
                    case 4:
                        String colorRoomchange = colors.getString("color_roomchange", getResources().getString(0+R.color.standardRoomChange));
                        img.setColorFilter(Color.parseColor(colorRoomchange));
                        break;
                    case 5:
                        String colorSpecial = colors.getString("color_special", getResources().getString(0+R.color.standardSpecial));
                        img.setColorFilter(Color.parseColor(colorSpecial));
                        break;
                    case 6:
                        img.setVisibility(View.GONE);
                        break;
                }
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);

        final SpectrumDialog.Builder spectrumDialog = new SpectrumDialog.Builder(getApplicationContext());
        spectrumDialog.setColors(R.array.pickerColors);
        spectrumDialog.setTitle(R.string.choose_color);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actualPosition = position;

                switch(actualPosition){
                    case 0:
                        actualColorString = colors.getString("color_cancel", getResources().getString(0+R.color.standardCancel));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 1:
                        actualColorString = colors.getString("color_change", getResources().getString(0+R.color.standardChange));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 2:
                        actualColorString = colors.getString("color_event", getResources().getString(0+R.color.standardEvent));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 3:
                        actualColorString = colors.getString("color_with_other", getResources().getString(0+R.color.standardWithOther));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 4:
                        actualColorString = colors.getString("color_roomchange", getResources().getString(0+R.color.standardRoomChange));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 5:
                        actualColorString = colors.getString("color_special", getResources().getString(0+R.color.standardSpecial));
                        spectrumDialog.setSelectedColor(Color.parseColor(actualColorString));
                        break;
                    case 6:
                        break;
                }

                if(actualPosition != 6){
                    spectrumDialog.build().show(getSupportFragmentManager(), "TAG");
                }
                //show reset dialog
                else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                    SharedPreferences.Editor edit = pref.edit();
                                    edit.putString("color_cancel", getResources().getString(0+R.color.standardCancel));
                                    edit.putString("color_change", getResources().getString(0+R.color.standardChange));
                                    edit.putString("color_event", getResources().getString(0+R.color.standardEvent));
                                    edit.putString("color_with_other", getResources().getString(0+R.color.standardWithOther));
                                    edit.putString("color_roomchange", getResources().getString(0+R.color.standardRoomChange));
                                    edit.putString("color_special", getResources().getString(0+R.color.standardSpecial));
                                    edit.commit();
                                    arrayAdapter.notifyDataSetChanged();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(R.string.sure_color_reset).setPositiveButton("Ja", dialogClickListener)
                            .setNegativeButton("Nein", dialogClickListener).show();
                }
            }
        });

        spectrumDialog.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor edit = pref.edit();

                //save the color
                switch(actualPosition){
                    case 0:
                        edit.putString("color_cancel", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                    case 1:
                        edit.putString("color_change", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                    case 2:
                        edit.putString("color_event", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                    case 3:
                        edit.putString("color_with_other", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                    case 4:
                        edit.putString("color_roomchange", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                    case 5:
                        edit.putString("color_special", String.format("#%06X", 0xFFFFFF & color));
                        edit.commit();
                        break;
                }

                //reload the list
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    //back arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class CustomAdapter extends BaseAdapter {

        private Context mContext;
        private String[]  Title;
        private int[] img;

        public CustomAdapter(Context context, String[] text1, int[] imageIds) {
            mContext = context;
            Title = text1;
            img = imageIds;

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return Title.length;
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View row;
            row = inflater.inflate(R.layout.list_item_color, parent, false);
            TextView title;
            ImageView i1;
            i1 = (ImageView) row.findViewById(R.id.color_image);
            title = (TextView) row.findViewById(R.id.color_text);
            title.setText(Title[position]);
            i1.setImageResource(img[position]);

            return (row);
        }
    }

}

