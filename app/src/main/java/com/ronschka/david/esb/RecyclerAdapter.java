package com.ronschka.david.esb;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<String> array;
    String date[] = new String[5];
    int year = Calendar.getInstance().get(Calendar.YEAR);

    public RecyclerAdapter(ArrayList<String> array) {
        this.array = array;
        date = array.get(0).split(",");
    }

    @Override
    public int getItemViewType(int position) {
        if(array.get(position + 1).contains("Keine Vertretungen") ||
                array.get(position + 1).contains("Vertretungen sind nicht freigegeben") ){
            return 2;
        }
        else{
            return 1;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 0:
                //void
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_void, parent, false));
            case 1:
                //filled
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_cancelled, parent, false));
            case 2:
                //null
                return null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Filled preset
        if(getItemViewType(position) == 1){
            //Case: Fällt aus
            if(array.get(position + 1).contains("Fällt aus")){
                String[] x = array.get(position + 1).split(" --- ");

                //in some cases their are more hours which are cancelled
                //they are marked by --- and if we count the
                // - divided by 3, we can exactly say how many
                //hours are cancelled in a row with the same teacher
                String s = array.get(position + 1);
                int counter = 0;
                for(int i = 0; i < s.length(); i++){
                    if(s.charAt(i) == '-'){
                        counter++;
                    }
                }
                int hours = 1;
                if(counter % 3 == 0){
                    hours = counter / 3;
                }

                //get the first hour (Ex. = Class, Hour, Teacher, --- ...)
                String[] y = x[0].split(" ");
                int startTime = Integer.parseInt(y[y.length - 2]);

                //getDayShort
                String day = "x";
                switch(position + 1){
                    case 1: day = "Mo";
                        break;
                    case 2: day = "Di";
                        break;
                    case 3: day = "Mi";
                        break;
                    case 4: day = "Do";
                        break;
                    case 5: day = "Fr";
                        break;
                }

                //get the Reason (if available)
                int positionStart = s.indexOf("Fällt aus");
                int positionEnd = s.indexOf(day, positionStart); //start counting from positionStart
                String reason = "";
                if((positionEnd != -1) && ((positionStart + 10) < (positionEnd - 1))){
                    reason = s.substring(positionStart + 10, positionEnd - 1);
                }

                //set teacherName in info
                holder.info.setText("Lehrer: " + x[0].substring(x[0].lastIndexOf(" ") + 1));

                //if the teachers are the same and there are more than one hour in row
                if(x[0].substring(x[0].lastIndexOf(" ") + 1).equals(x[1].substring(x[1].lastIndexOf(" ") + 1))
                        && hours > 1){
                    String time = startTime + "-" + (startTime + hours - 1);
                    holder.hour.setText(time);

                    if(!reason.equals("")){
                        //if there is a reason replace teacherName with reason
                        holder.info.setText(reason);
                        //and set teacher name in teacher TextView
                        holder.teacher.setText("Lehrer: " + x[0].substring(x[0].lastIndexOf(" ") + 1));
                    }
                }
                //4 times "-" means cancelled over more hours separated by a additional "-"
                //e.g. string: Mi 12.7. GO12 5 - 6 TEACHER --- Fällt aus
                else if(counter == 4){
                    //!startTime is in this case the endTime
                    String time = Integer.parseInt(y[y.length - 4]) + " - " + startTime;
                    holder.hour.setText(time);
                }
                else{
                    holder.hour.setText(Integer.toString(startTime));
                }
            }
            //Case: Mitbetreuung
            else if(array.get(position + 1).contains("Mitbetreuung")){
                String[] y = array.get(position + 1).split("Mitbetreuung");

                holder.head.setText("Mitbetreuung");
                holder.head.setTextColor(Color.parseColor("#7B1FA2"));
                holder.hour.setTextColor(Color.parseColor("#7B1FA2"));

                //if no reason is added
                if(y.length == 1) {

                    String[] x = array.get(position + 1).split(" ");
                    String room = x[x.length - 2];
                    String teacher = x[x.length - 3];

                    holder.info.setText("Lehrer: " + teacher + ", in Raum: " + room);

                    if (array.get(position + 1).contains("-")) {
                        holder.hour.setText(x[x.length - 6] + "-" + x[x.length - 4]);
                    }
                    else {
                        holder.hour.setText(x[x.length - 4]);
                    }
                }
                //there is a reason
                else{
                    y[1] = y[1].replaceFirst(" ", "");
                    holder.info.setText(y[1]);
                    String[] split = y[0].split(" ");
                    String room = split[split.length - 1];
                    String teacher = split[split.length - 2];

                    holder.teacher.setText(room + " | " + teacher);

                    if (y[0].contains("-")) {
                        holder.hour.setText(split[split.length - 5] + "-" + split[split.length - 3]);
                    }
                    else {
                        holder.hour.setText(split[split.length - 3]);
                    }
                }
            }
            //case: Raumwechsel
            else if(array.get(position + 1).contains("Anderer Raum!")){
                holder.head.setText("Raumwechsel");
                holder.head.setTextColor(Color.parseColor("#00695C"));
                holder.hour.setTextColor(Color.parseColor("#00695C"));

                String x[] = array.get(position + 1).split(" ");
                holder.info.setText("Lehrer: " + x[x.length - 4] + ", in Raum: " + x[x.length - 3]);

                //more hours
                if(array.get(position + 1).contains("-")){
                    holder.hour.setText(x[x.length - 7] + "-" + x[x.length - 5]);
                }
                else{
                    holder.hour.setText(x[x.length - 5]);
                }
            }
        }

        //Void preset
        else{
            if(array.get(position + 1).contains("Vertretungen sind nicht freigegeben")) {
                holder.cancel.setText("Vertretungen sind nicht freigegeben");
            }
        }

        switch(position + 1){
            case 1:
                holder.day.setText("Montag, " + date[position] + year);
                break;
            case 2:
                holder.day.setText("Dienstag, " + date[position] + year);
                break;
            case 3:
                holder.day.setText("Mittwoch, " + date[position] + year);
                break;
            case 4:
                holder.day.setText("Donnerstag, " + date[position] + year);
                break;
            case 5:
                holder.day.setText("Freitag, " + date[position] + year);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return array.size() - 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView info, head, hour, day, teacher, cancel;
        RelativeLayout defaultRelative;

        public ViewHolder(View itemView){
            super(itemView);
            defaultRelative = (RelativeLayout)itemView.findViewById(R.id.relativDefault);

            cancel = (TextView)itemView.findViewById(R.id.txtCancel);
            teacher = (TextView)itemView.findViewById(R.id.txtTeacher);
            day = (TextView)itemView.findViewById(R.id.txtDay);
            hour = (TextView)itemView.findViewById(R.id.txtHour);
            head = (TextView)itemView.findViewById(R.id.txtHead);
            info = (TextView)itemView.findViewById(R.id.txtCancel);
        }
    }
}
