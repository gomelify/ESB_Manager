package com.ronschka.david.esb.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ronschka.david.esb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomAdapter {

    /**
     * Returns a SimpleAdapter that uses the layout "listview_entry_hw".
     *
     * @param c Needed for {@link android.widget.SimpleAdapter}.
     * @param a ArrayList with HashMaps to show with the adapter.
     */
    public static SimpleAdapter entry(final Context c, final ArrayList<HashMap<String, String>> a, boolean type) {
        final String[] from = {"URGENT", "SUBJECT", "HOMEWORK", "UNTIL", "COLOR"};
        // All TextViews in Layout "listview_entry_hw"
        final int[] to = {R.id.textView_urgent, R.id.textView_subject,
                R.id.textView_homework, R.id.textView_until, R.id.colorStringOutput};

        if(type){ //type true means homework
            // Make a SimpleAdapter which is like a row in the homework list
            return new SimpleAdapter(c, a, R.layout.listview_entry_hw, from, to){

                //make the testStringOutupt disappear and parse given Color to the RectangleColorMarker
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView txt = v.findViewById(to[4]);
                    txt.setVisibility(View.GONE);
                    ImageView imageView = v.findViewById(R.id.colorRecHomework);
                    imageView.setBackgroundColor(Color.parseColor(a.get(position).get("COLOR")));

                    ImageView imgStar = v.findViewById(R.id.imgImportant);

                    if(a.get(position).get("URGENT").equals(c.getString(R.string.important))){
                        imgStar.setVisibility(View.VISIBLE);
                        imgStar.setColorFilter(Color.parseColor(c.getResources().getString(0+R.color.MaterialAmber)));
                    }
                    else{
                        imgStar.setVisibility(View.GONE);
                    }

                    return v;
                }
            };
        }
        else{
            // Make a SimpleAdapter which is like a row in the exam list
            return new SimpleAdapter(c, a, R.layout.listview_entry_ex, from, to){

                //make the testStringOutupt disappear and parse given Color to the RectangleColorMarker
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView txt = v.findViewById(to[4]);
                    txt.setVisibility(View.GONE);
                    ImageView imageView = v.findViewById(R.id.colorRecHomework);
                    imageView.setBackgroundColor(Color.parseColor(a.get(position).get("COLOR")));

                    return v;
                }
            };
        }
    }

    /**
     * Converts an ArrayList containing HashMaps to a List containing a List Containing a Map.
     *
     * @param a   ArrayList with HashMaps to convert.
     * @param row Row to add to the Map.
     */
    private static List<List<Map<String, String>>> covertToListListMap(final ArrayList<HashMap<String, String>> a, final String row) {
        final List<List<Map<String, String>>> ll = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            final Map<String, String> tmpL = new HashMap<>();
            tmpL.put(row, a.get(i).get(row));

            final List<Map<String, String>> l = new ArrayList<>();
            l.add(tmpL);

            ll.add(l);
        }
        return ll;
    }
}
