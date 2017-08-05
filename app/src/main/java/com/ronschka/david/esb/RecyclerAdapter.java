package com.ronschka.david.esb;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    ArrayList<String> array;
    MainActivity mainActivity;
    String date[] = new String[5];
    String fullInformationStr; //this String contains redundancy controlled information
    int currentViewType = 0;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    public RecyclerAdapter(ArrayList<String> array, MainActivity mainAct) {
        this.mainActivity = mainAct;
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

        fullInformationStr = getRedundancy(array.get(position + 1)); //String with redundancy control

        if(fullInformationStr.split(" ~ ").length > 1){ //splitter character -> more than one cases

            //switch between the different extended cards
            switch(fullInformationStr.split(" ~ ").length + 1){ //redundancy doesn't need extra cards
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
        int infoQuantity = 1; //how many info cards
        String dateString = "";

        //                  !!! FILL THE CARDS WITH DATE-INFORMATION  !!!
        //null presets don't contain a day TextView
        if (currentViewType > 0) {
            switch (position + 1) {
                case 1:
                    dateString = "Montag, " + date[position] + year;
                    holder.day.setText(dateString);
                    break;
                case 2:
                    dateString = "Dienstag, " + date[position] + year;
                    holder.day.setText(dateString);
                    break;
                case 3:
                    dateString = "Mittwoch, " + date[position] + year;
                    holder.day.setText(dateString);
                    break;
                case 4:
                    dateString = "Donnerstag, " + date[position] + year;
                    holder.day.setText(dateString);
                    break;
                case 5:
                    dateString = "Freitag, " + date[position] + year;
                    holder.day.setText(dateString);
                    break;
            }
        }

        //                      !!! FILL THE CARDS WITH DETAILS !!!
        //Filled preset
        if (currentViewType > 1) {

            //save parsed data in specified strings
            String strHours, strHead, strInfo, strColor, strBackColor; //needed for simple cardView
            String detailTeacher, detailInfo, detailRoom; //needed for detail cardView

            //default values
            strHours = "1-2";
            strHead = "Entfall";
            strInfo = "Herr Beispiel ist ganz dolle krank :(";
            strColor = "#C62828";
            strBackColor = "";

            //new list for information needed for split
            List<String> infoList;

            //catch event: more info-cards
            if (currentViewType > 2) {
                infoQuantity = currentViewType - 1; //amount of cards
                infoList = new ArrayList<>(Arrays.asList(fullInformationStr
                        .split(" ~ "))); //multiple info = split them in list
            } else {
                infoList = new ArrayList<>();
                infoList.add(0, fullInformationStr); //no multiple info = default string info
            }

            for (int actualInfoCounter = 0; actualInfoCounter < infoQuantity; actualInfoCounter++) {

                String infoString = infoList.get(actualInfoCounter);
                infoString = infoString.trim();

                Log.d("ESBLOG", "Fragment: " + "Nr. " + actualInfoCounter + " / " + (position + 1) + ": " + infoString);

                //reset details
                detailTeacher = " ";
                detailInfo = " ";
                detailRoom = " ";

                //Case: Fällt aus
                if (infoString.contains("Fällt aus")) {

                    strHead = "Entfall";
                    strColor = "#C62828";

                    String[] splitter = infoString.split(" ");
                    String info[];

                    if (splitter[3].equals("-")) { //more hours e.g. 1 - 2
                        strHours = splitter[2] + "-" + splitter[4];
                        info = infoString.split(" ", 10);
                        detailTeacher = splitter[5];

                        if (info.length == 10) {//only if reason exists
                            detailInfo = info[9];
                            strInfo = detailInfo + ", Lehrer: " + detailTeacher;
                        } else {
                            strInfo = "Lehrer: " + detailTeacher;
                        }
                    } else { //only one hour
                        strHours = splitter[2];
                        info = infoString.split(" ", 8);

                        if (info.length == 8) {//only if reason exists
                            detailInfo = info[7];
                            detailTeacher = splitter[3];
                            strInfo =  detailInfo + ", Lehrer: " + detailTeacher;
                        } else {
                            strInfo = "Lehrer: " + detailTeacher;
                        }
                    }
                }

                //Case: Mitbetreuung
                else if (infoString.contains("Mitbetreuung")) {
                    String[] splitter = infoString.split("Mitbetreuung");

                    strHead = "Mitbetreuung";
                    strColor = "#7B1FA2";

                    //if no reason is added
                    if (splitter.length == 1) {

                        String[] x = infoString.split(" ");
                        detailRoom = x[x.length - 2];
                        detailTeacher = x[x.length - 3];

                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;

                        if (infoString.contains("-")) {
                            strHours = x[x.length - 6] + "-" + x[x.length - 4];
                        } else {
                            strHours = x[x.length - 4];
                        }
                    }
                    //there is a reason
                    else {
                        splitter[1] = splitter[1].replaceFirst(" ", "");
                        String[] split = splitter[0].split(" ");

                        detailInfo = splitter[1];
                        detailRoom = split[split.length - 1];
                        detailTeacher = split[split.length - 2];

                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom + ", Info: " + detailInfo;

                        if (splitter[0].contains("-")) {
                            strHours = split[split.length - 5] + "-" + split[split.length - 3];
                        } else {
                            strHours = split[split.length - 3];
                        }
                    }
                }

                //Case: Raumwechsel
                else if (infoString.contains("Anderer Raum!")) {
                    strHead = "Raumwechsel";
                    strColor = "#01579B";

                    String splitter[] = infoString.split(" ");

                    detailTeacher = splitter[5];
                    detailRoom = splitter[6];

                    if (!splitter[3].equals("-")) {
                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        strHours = splitter[3];
                    } else { //hours like 1-2 (not redundant type)
                        strHours = splitter[2] + "-" + splitter[4];
                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                    }
                }
                //Case: Veranstaltung
                else if(infoString.contains("Veranst.")){
                    strHead = "Veranstaltung";
                    strColor = "#E65100";

                    String newInfoString = infoString.replaceAll("   "," ");
                    String splitter[] = newInfoString.split(" " , 5);

                    strHours = splitter[2];
                    detailInfo = splitter[4];
                    strInfo = detailInfo;
                }
                else if(infoString.contains("Nachrichten zum Tag")){
                    strHead = "Nachricht des Tages";
                    strColor = "#FFFFFF";
                    strBackColor = "#303F9F";
                    strHours = "";

                    String newInfoString = infoString.replaceAll("   "," ");
                    String splitter[] = newInfoString.split(" " , 5);

                    detailInfo = splitter[4];
                    strInfo = detailInfo;
                }
                //Case: Vertretung
                else if(!infoString.isEmpty()) {
                    strHead = "Vertretung";
                    strColor = "#2E7D32";

                    String splitter[] = infoString.split(" ");

                    if (splitter[3].contains("-")) {
                        strHours = splitter[2] + "-" + splitter[4];
                        detailTeacher = splitter[5];
                        detailRoom = splitter[6];

                        if(splitter.length > 5){
                            strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        }
                        else{
                            strInfo = "Lehrer: " + detailTeacher;
                        }
                    } else {
                        strHours = splitter[2];
                        detailTeacher = splitter[3];

                        if(splitter.length > 4){
                            detailRoom = splitter[4];
                            strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        }
                        else{
                            strInfo = "Lehrer: " + detailTeacher;
                        }
                    }
                }

                //put info to textViews, switch-case -> different quantities -> different textViews
                //also put onClick on different relativeLayouts of the cards
                switch (actualInfoCounter + 1) {
                    case 1:
                        //set data
                        holder.head1.setText(strHead);
                        holder.hour1.setText(strHours);
                        holder.info1.setText(strInfo);

                        //set color
                        holder.head1.setTextColor(Color.parseColor(strColor));
                        holder.hour1.setTextColor(Color.parseColor(strColor));

                        //click Listener
                        final String finalStrHead = strHead;
                        final String finalStrHours = strHours;
                        final String finalStrColor = strColor;
                        final String finalStrDate = dateString;
                        final String finalStrInfo = detailInfo;
                        final String finalStrTeacher = detailTeacher;
                        final String finalStrRoom = detailRoom;

                        //Case: Nachrichten zum Tag! could only be the first one
                        if(!strBackColor.equals("")){
                            holder.card1.setBackgroundColor(Color.parseColor(strBackColor));
                            holder.info1.setTextColor(Color.parseColor(strColor));
                            holder.hour1.setVisibility(View.GONE);
                        }

                        holder.card1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("ESBLOG", "CLICK! " + finalStrHead + " " + finalStrHours +  " " + finalStrInfo);

                                String detail = finalStrHead + "," + finalStrHours + "," + finalStrColor
                                        + "," + finalStrDate + "," + finalStrInfo + "," + finalStrTeacher;
                                mainActivity.onCreateDetailView(detail);
                            }
                        });
                        break;
                    case 2:
                        //set data
                        holder.head2.setText(strHead);
                        holder.hour2.setText(strHours);
                        holder.info2.setText(strInfo);

                        //set color
                        holder.head2.setTextColor(Color.parseColor(strColor));
                        holder.hour2.setTextColor(Color.parseColor(strColor));

                        //click Listener
                        final String finalStrHead2 = strHead;
                        final String finalStrHours2 = strHours;
                        final String finalStrColor2 = strColor;
                        final String finalStrDate2 = dateString;
                        final String finalStrInfo2 = strInfo;
                        final String finalStrTeacher2 = detailTeacher;

                        holder.card2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String detail = finalStrHead2 + "," + finalStrHours2 + "," + finalStrColor2 + ","
                                        + finalStrDate2 + "," + finalStrInfo2 + "," + finalStrTeacher2;
                                mainActivity.onCreateDetailView(detail);
                            }
                        });
                        break;
                    case 3:
                        //set data
                        holder.head3.setText(strHead);
                        holder.hour3.setText(strHours);
                        holder.info3.setText(strInfo);

                        //set color
                        holder.head3.setTextColor(Color.parseColor(strColor));
                        holder.hour3.setTextColor(Color.parseColor(strColor));

                        //click Listener
                        final String finalStrHead3 = strHead;
                        final String finalStrHours3 = strHours;
                        final String finalStrColor3 = strColor;
                        final String finalStrDate3 = dateString;
                        final String finalStrInfo3 = strInfo;
                        final String finalStrTeacher3 = detailTeacher;

                        holder.card3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String detail = finalStrHead3 + "," + finalStrHours3 + "," + finalStrColor3 + ","
                                        + finalStrDate3 + "," + finalStrInfo3 + "," + finalStrTeacher3;
                                mainActivity.onCreateDetailView(detail);
                            }
                        });
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return array.size() - 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView day, info1, head1, hour1,
                info2, head2, hour2,
                info3, head3, hour3;

        RelativeLayout card1, card2, card3;

        public ViewHolder(View itemView){
            super(itemView);

            day = (TextView)itemView.findViewById(R.id.txtDay);

            card1 = (RelativeLayout)itemView.findViewById(R.id.relativDefault);
            hour1 = (TextView)itemView.findViewById(R.id.txtHour);
            head1 = (TextView)itemView.findViewById(R.id.txtHead);
            info1 = (TextView)itemView.findViewById(R.id.txtInfoDetail);

            card2 = (RelativeLayout)itemView.findViewById(R.id.relativDefault2);
            hour2 = (TextView)itemView.findViewById(R.id.txtHour2);
            head2 = (TextView)itemView.findViewById(R.id.txtHead2);
            info2 = (TextView)itemView.findViewById(R.id.txtInfo2);

            card3 = (RelativeLayout)itemView.findViewById(R.id.relativDefault3);
            hour3 = (TextView)itemView.findViewById(R.id.txtHour3);
            head3 = (TextView)itemView.findViewById(R.id.txtHead3);
            info3 = (TextView)itemView.findViewById(R.id.txtInfo3);
        }
    }

    //in some cases there are redundant listings so they can be
    //summarized in one card, the function checks if the case is the same
    public String getRedundancy(String checkString){

        String info[] = checkString.split(" ~ ");
        int counter = info.length;

        if(counter < 2){
            return checkString; // no redundancy -> return parameter
        }
        else{
            String check1[], check2[], check3[], check4[];
            String newHours;

            if(counter == 2){
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);

                //they have same info
                if(check1[3].equals(check2[3])){
                    newHours = " " + check1[2] + " - " + check2[2] + " "; //important spaces because we delete the old in the replace process
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0].replace(" " + check1[2] + " ", newHours); //replace old String hours with new one
                }
                else{
                    Log.d("ESBLOG", "redun FALSE!");
                    return checkString; //no redundancies -> return parameter
                }
            }
            else if(counter == 3) {
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                info[2] = info[2].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);
                check3 = info[2].split(" ", 4);

                //they have same info
                if (check1[3].equals(check2[3])) {
                    newHours = " " + check1[2] + " - " + check2[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0].replace(" " + check1[2] + " ", newHours) + " ~ " + info[2];
                } else if (check2[3].equals(check3[3])) {
                    newHours = " " + check2[2] + " - " + check3[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0] + " ~ " + info[1].replace(" " + check2[2] + " ", newHours);
                } else if (check1[3].equals(check2[3]) && check2[3].equals(check3[3])) {
                    newHours = " " + check1[2] + " - " + check2[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0].replace(" " + check1[2] + " ", newHours); //replace old String hours with new one
                } else {
                    Log.d("ESBLOG", "redun FALSE!");
                    return checkString;
                }
            }
            else if(counter == 4){
                info[0] = info[0].trim();
                info[1] = info[1].trim();
                info[2] = info[2].trim();
                info[3] = info[3].trim();
                check1 = info[0].split(" ", 4);
                check2 = info[1].split(" ", 4);
                check3 = info[2].split(" ", 4);
                check4 = info[3].split(" ", 4);

                //they have same info
                if (check1[3].equals(check2[3])) {
                    newHours = " " + check1[2] + " - " + check2[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0].replace(" " + check1[2] + " ", newHours) + " ~ " + info[2] + " ~ " + info[3];
                } else if (check2[3].equals(check3[3])) {
                    newHours = " " + check2[2] + " - " + check3[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0] + " ~ " + info[1].replace(" " + check2[2] + " ", newHours) + " ~ " + info[3];
                } else if (check3[3].equals(check4[3])) {
                    newHours = " " + check3[2] + " - " + check4[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0] + " ~ " + info[1] +  " ~ " + info[2].replace(" " + check3[2] + " ", newHours);
                } else if (check1[3].equals(check2[3]) && check2[3].equals(check3[3])) {
                    newHours = " " + check1[2] + " - " + check2[2] + " ";
                    Log.d("ESBLOG", "redun TRUE!");
                    return info[0].replace(" " + check1[2] + " ", newHours); //replace old String hours with new one
                } else {
                    Log.d("ESBLOG", "redun FALSE!");
                    return checkString;
                }
            }

            Log.d("ESBLOG", "redun FALSE!");
            return checkString;
        }
    }
}


