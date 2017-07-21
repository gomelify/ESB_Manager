package com.ronschka.david.esb;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<String> array;
    String date[] = new String[5];
    int currentViewType = 0;
    int redundancyComponents = 0;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    String redundancyHours = "";

    public RecyclerAdapter(ArrayList<String> array) {
        this.array = array;
        date = array.get(0).split(","); //array 0 is date in string
    }

    @Override
    public int getItemViewType(int position) {
        if (array.get(position + 1).contains("Vertretungen sind nicht freigegeben")){
            currentViewType = 0;
            return 0; //show nothing
        }
        else if(array.get(position + 1).contains("Keine Vertretungen")) {
            currentViewType = 1;
            return 1; //show void card
        }
        else if(array.get(position + 1).split(" Mo | Di | Mi | Do | Fr ").length > 2 &&
                !checkRedundancy(array.get(position + 1))){

            //switch between the different extended cards
            switch(array.get(position + 1).split(" Mo | Di | Mi | Do | Fr ").length
                    - redundancyComponents){ //redundancy doesn't need extra cards
                case 3:
                    currentViewType = 3;
                    return 3;
                case 4:
                    currentViewType = 4;
                    return 4;
                default:
                    currentViewType = 0;
                    return 0;
            }
        }
        else{
            //default simple filled card
            currentViewType = 2;
            return 2;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 0:
                //null
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_null, parent, false));
            case 1:
                //void
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_void, parent, false));
            case 2:
                //filled 1
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_filled, parent, false));
            case 3:
                //extended 2
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x1, parent, false));
            case 4:
                //extended 3
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x2, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean nullTag = false; //track if the object is a "null"-object
        int infoQuantity = 1; //how many info cards

        //Filled preset
        if(currentViewType > 1){

            //save parsed data in specified strings
            String strHours, strHead, strInfo, strTeacher, strColor;

            //default values
            strHours = "1-2";
            strHead = "Entfall";
            strInfo = "Herr Beispiel ist ganz dolle krank :(";
            strTeacher = "";
            strColor = "#C62828";

            //new list for information needed for split
            List<String> infoList;

            //catch event: more info-cards
            if(currentViewType > 2){
                infoQuantity = currentViewType - 1; //amount of cards
                infoList = new ArrayList<>(Arrays.asList(array.get(position + 1)
                        .split(" Mo | Di | Mi | Do | Fr "))); //multiple info = split them in list
            }
            else {
                infoList = new ArrayList<>();
                infoList.add(0 , array.get(position + 1)); //no multiple info = default string info
            }


            for(int actualInfoCounter = 0; actualInfoCounter < infoQuantity; actualInfoCounter++) {

                //Case: Fällt aus
                if (infoList.get(actualInfoCounter).contains("Fällt aus")) {

                    String[] splitter = infoList.get(actualInfoCounter).split(" ");
                    if (!redundancyHours.isEmpty()) {
                        strHours = redundancyHours;

                        String infoString = infoList.get(actualInfoCounter);

                        //because it don't split with normal filled cardView
                        String infoArray[] = infoString.split(" Mo | Di | Mi | Do | Fr ");
                        String splitArray[] = infoArray[1].split(" ", 8); //split to the limit where reason begins

                        //get the Reason (if available)
                        if (splitArray.length == 8) { //only if reason exists
                            strInfo = splitArray[7];
                            strTeacher = splitArray[3]; //if there is a info, the teacher has his own textView
                        } else {
                            strInfo = "Lehrer: " + splitArray[3];
                        }
                    } else {
                        strHours = splitter[2];

                        String info[] = infoList.get(actualInfoCounter).split(" ", 6);

                        //get the Reason (if available)
                        if (info.length == 6) {
                            strInfo = info[5];
                            strTeacher = splitter[3];
                        } else {
                            strInfo = "Lehrer: " + splitter[5];
                        }
                    }
                }

                //Case: Mitbetreuung
                else if (array.get(position + 1).contains("Mitbetreuung")) {
                    String[] y = array.get(position + 1).split("Mitbetreuung");

                    strHead = "Mitbetreuung";
                    strColor = "#7B1FA2";

                    //if no reason is added
                    if (y.length == 1) {

                        String[] x = array.get(position + 1).split(" ");
                        String room = x[x.length - 2];
                        String teacher = x[x.length - 3];

                        strInfo = "Lehrer: " + teacher + ", in Raum: " + room;

                        if (array.get(position + 1).contains("-")) {
                            strHours = x[x.length - 6] + "-" + x[x.length - 4];
                        } else {
                            strHours = x[x.length - 4];
                        }
                    }
                    //there is a reason
                    else {
                        y[1] = y[1].replaceFirst(" ", "");
                        strInfo = y[1];
                        String[] split = y[0].split(" ");
                        String room = split[split.length - 1];
                        String teacher = split[split.length - 2];

                        strTeacher = room + " | " + teacher;

                        if (y[0].contains("-")) {
                            strHours = split[split.length - 5] + "-" + split[split.length - 3];
                        } else {
                            strHours = split[split.length - 3];
                        }
                    }
                }

                //Case: Raumwechsel
                else if (array.get(position + 1).contains("Anderer Raum!")) {
                    strHead = "Raumwechsel";
                    strColor = "#00695C";

                    String x[] = array.get(position + 1).split(" ");
                    strInfo = "Lehrer: " + x[x.length - 4] + ", in Raum: " + x[x.length - 3];

                    //more hours
                    if (array.get(position + 1).contains("-")) {
                        strHours = x[x.length - 7] + "-" + x[x.length - 5];
                    } else {
                        strHours = x[x.length - 5];
                    }
                }

                //put info to textViews, switch-case -> different quantities -> different textViews
                switch(actualInfoCounter + 1){
                    case 1:
                        //set data
                        holder.head1.setText(strHead);
                        holder.hour1.setText(strHours);
                        holder.info1.setText(strInfo);
                        holder.teacher1.setText(strTeacher);

                        //set color
                        holder.head1.setTextColor(Color.parseColor(strColor));
                        holder.hour1.setTextColor(Color.parseColor(strColor));
                        break;
                    case 2:
                        //set data
                        holder.head2.setText(strHead);
                        holder.hour2.setText(strHours);
                        holder.info2.setText(strInfo);
                        holder.teacher2.setText(strTeacher);

                        //set color
                        holder.head2.setTextColor(Color.parseColor(strColor));
                        holder.hour2.setTextColor(Color.parseColor(strColor));
                        break;
                }
            }
        }
        //null preset
        else if(currentViewType == 0){
            nullTag = true; //null views don't have a day text!
        }

        if(nullTag == false) {
            switch (position + 1) {
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
    }

    @Override
    public int getItemCount() {
        return array.size() - 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView day, info1, head1, hour1,  teacher1,
                info2, head2, hour2, teacher2;


        public ViewHolder(View itemView){
            super(itemView);

            day = (TextView)itemView.findViewById(R.id.txtDay);

            teacher1 = (TextView)itemView.findViewById(R.id.txtTeacher);
            hour1 = (TextView)itemView.findViewById(R.id.txtHour);
            head1 = (TextView)itemView.findViewById(R.id.txtHead);
            info1 = (TextView)itemView.findViewById(R.id.txtInfo);

            teacher2 = (TextView)itemView.findViewById(R.id.txtTeacher2);
            hour2 = (TextView)itemView.findViewById(R.id.txtHour2);
            head2 = (TextView)itemView.findViewById(R.id.txtHead2);
            info2 = (TextView)itemView.findViewById(R.id.txtInfo2);
        }
    }

    //in some cases there are redundant listings so they can be
    //summarized in one card, the function checks if the case is the same
    public boolean checkRedundancy(String checkString){

        String info[] = checkString.split(" Mo | Di | Mi | Do | Fr ");
        int counter = info.length - 1 ;

        if(counter < 2){
            redundancyHours = "";
            return false; // no redundancy
        }
        else{
            String check1[], check2[], check3[], check4[];
            String date;

            if(counter == 2){
                info[0].replaceFirst(" ", "");
                info[1].replaceFirst(" ", "");
                check1 = info[1].split(" ", 4);
                check2 = info[2].split(" ", 4);

                if(check1[3].equals(check2[3])){
                    date = check1[2] + "-" + check2[2];
                    redundancyHours = date;
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(counter == 3){

            }
            return false;
        }
    }
}
