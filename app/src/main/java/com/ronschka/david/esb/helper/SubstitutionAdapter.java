package com.ronschka.david.esb.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ronschka.david.esb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SubstitutionAdapter extends RecyclerView.Adapter<SubstitutionAdapter.SubstitutionRecyclerViewHolders>{
    private ArrayList<String> array;
    private String cancelColor, withOtherColor, roomchangeColor, eventColor, changeColor, specialColor;
    private Context context;
    private ArrayList<String> fullInformation;
    private String[] date;
    int currentViewType = 0;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    public SubstitutionAdapter(ArrayList<String> array, Context context) {
        this.context = context;
        this.array = array;
        this.date = array.get(array.size() - 1).split(",");

        //Preference for color values
        final SharedPreferences colors = PreferenceManager.getDefaultSharedPreferences(context);

        //colors for the different cases
        cancelColor = colors.getString("color_cancel", context.getResources().getString(0+ R.color.standardCancel));
        withOtherColor = colors.getString("color_with_other", context.getResources().getString(0+R.color.standardWithOther));
        roomchangeColor = colors.getString("color_roomchange", context.getResources().getString(0+R.color.standardRoomChange));
        eventColor = colors.getString("color_event", context.getResources().getString(0+R.color.standardEvent));
        changeColor = colors.getString("color_change", context.getResources().getString(0+R.color.standardChange));
        specialColor = colors.getString("color_special", context.getResources().getString(0+R.color.standardSpecial));
    }

    @Override
    public int getItemViewType(int position) {
        if (array.get(position).contains("Vertretungen sind nicht freigegeben")){
            currentViewType = 0;
            return 0; //show nothing
        }
        else if(array.get(position).contains("Keine Vertretungen") &&
                !array.get(position).contains("Nachrichten zum Tag")) {
            currentViewType = 1;
            return 1; //show void card
        }

        fullInformation = new ArrayList<>(
                Arrays.asList(array.get(position).split(" SPLIT ")));

        if(fullInformation.size() > 1){ //-> more than one cases
            //switch between the different extended cards
            switch(fullInformation.size() + 1){
                case 3:
                    currentViewType = 3;
                    return 3;
                case 4:
                    currentViewType = 4;
                    return 4;
                case 5:
                    currentViewType = 5;
                    return 5;
                case 6:
                    currentViewType = 6;
                    return 6;
                default:
                    currentViewType = -1;
                    return -1;
            }
        }
        else{
            //default simple filled card
            currentViewType = 2;
            return 2;
        }
    }

    @Override
    public SubstitutionRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case -1:
                //error
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_error, parent, false));
            case 0:
                //null
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_null, parent, false));
            case 1:
                //void
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_void, parent, false));
            case 2:
                //filled 1
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_filled, parent, false));
            case 3:
                //extended 2
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x1, parent, false));
            case 4:
                //extended 3
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x2, parent, false));
            case 5:
                //extended 4
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x3, parent, false));
            case 6:
                //extended 4
                return new SubstitutionRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_child_x4, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(SubstitutionRecyclerViewHolders holder, int position) {
        //                      !!! FILL THE CARDS WITH DETAILS !!!
        //Filled preset
        if (currentViewType > 1) {
            for (int actualInfoCounter = 0; actualInfoCounter < fullInformation.size(); actualInfoCounter++) {
                //explanation: 0 - day, 1 - date, 2 - class, 3 - hours, 4 - teacher, 5 - room, 6 - case, 7 - infoText
                List<String> infoList = new ArrayList<>(Arrays.asList(fullInformation.get(actualInfoCounter).split("~")));

                String strHours = infoList.get(3).replace(" ",""),      //needed for simple cardView
                        strInfo, strHead, strColor;

                String detailDate = infoList.get(1),                    //needed for detail cardView
                        detailClass = infoList.get(2),
                        detailTeacher = infoList.get(4),
                        detailRoom = infoList.get(5),
                        detailInfo = infoList.get(7);

                switch(infoList.get(6)){
                    case "Fällt aus":
                        strHead = "Entfall";
                        strColor = cancelColor;
                        strInfo = detailInfo + ", Lehrer: " + detailTeacher;
                        break;
                    case "Mitbetreuung":
                        strHead = "Mitbetreuung";
                        strColor = withOtherColor;
                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        break;
                    case "Raumwechsel":
                        strHead = "Raumwechsel";
                        strColor = roomchangeColor;
                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        break;
                    case "Veranst.":
                        strHead = "Veranstaltung";
                        strColor = eventColor;
                        strInfo = detailInfo + " in " + detailRoom;
                        break;
                    case "Anderer Raum!":
                        strHead = "Raumwechsel";
                        strColor = roomchangeColor;
                        strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        break;
                    case "Nachrichten zum Tag":
                        strHead = "Nachricht des Tages";
                        strColor = specialColor;
                        strHours = "";
                        strInfo = detailInfo;
                        break;
                    default: //Vertretung
                        strHead = "Vertretung";
                        strColor = changeColor;
                        if(!detailRoom.isEmpty()){
                            strInfo = "Lehrer: " + detailTeacher + ", in Raum: " + detailRoom;
                        }
                        else{
                            strInfo = "Lehrer: " + detailTeacher;
                        }
                        break;
                }

                //put info to textViews, switch-case -> different quantities -> different textViews
                //also put onClick on different relativeLayouts of the cards
                switch (actualInfoCounter + 1) {
                    case 1:
                        //uses to much space
                        if(strHours.length() > 4){
                            holder.hour1.setTextSize(22);
                        }

                        //set data
                        holder.head1.setText(strHead);
                        holder.hour1.setText(strHours);
                        holder.info1.setText(strInfo);

                        //set color
                        holder.head1.setTextColor(Color.parseColor(strColor));
                        holder.hour1.setTextColor(Color.parseColor(strColor));

                        final String[] detailEntry = {strHead, strHours, strColor, (detailDate + year),
                            detailClass, detailRoom, detailTeacher, detailInfo};

                        //Case: Nachrichten zum Tag! can only be the first one
                        if(strColor.equals(specialColor)){
                            holder.hour1.setVisibility(View.GONE);
                            //special height
                            holder.head1.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.special_event_height);
                            //special padding
                            int paddingPixel = 66;
                            float density = context.getResources().getDisplayMetrics().density;
                            int paddingDp = (int)(paddingPixel * density);
                            holder.head1.setPadding(paddingDp, 0, 0, 0);
                        }

                        holder.card1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreateSubstitutionDetailView(detailEntry);
                            }
                        });
                        break;
                    case 2:
                        //uses to much space
                        if(strHours.length() > 4){
                            holder.hour2.setTextSize(22);
                        }

                        //set data
                        holder.head2.setText(strHead);
                        holder.hour2.setText(strHours);
                        holder.info2.setText(strInfo);

                        //set color
                        holder.head2.setTextColor(Color.parseColor(strColor));
                        holder.hour2.setTextColor(Color.parseColor(strColor));

                        final String[] detailEntry2 = {strHead, strHours, strColor, (detailDate + year),
                                detailClass, detailRoom, detailTeacher, detailInfo};

                        holder.card2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreateSubstitutionDetailView(detailEntry2);
                            }
                        });
                        break;
                    case 3:
                        //uses to much space
                        if(strHours.length() > 4){
                            holder.hour3.setTextSize(22);
                        }

                        //set data
                        holder.head3.setText(strHead);
                        holder.hour3.setText(strHours);
                        holder.info3.setText(strInfo);

                        //set color
                        holder.head3.setTextColor(Color.parseColor(strColor));
                        holder.hour3.setTextColor(Color.parseColor(strColor));

                        final String[] detailEntry3 = {strHead, strHours, strColor, (detailDate + year),
                                detailClass, detailRoom, detailTeacher, detailInfo};

                        holder.card3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreateSubstitutionDetailView(detailEntry3);
                            }
                        });
                        break;
                    case 4:
                        //uses to much space
                        if(strHours.length() > 4){
                            holder.hour4.setTextSize(22);
                        }

                        //set data
                        holder.head4.setText(strHead);
                        holder.hour4.setText(strHours);
                        holder.info4.setText(strInfo);

                        //set color
                        holder.head4.setTextColor(Color.parseColor(strColor));
                        holder.hour4.setTextColor(Color.parseColor(strColor));

                        final String[] detailEntry4 = {strHead, strHours, strColor, (detailDate + year),
                                detailClass, detailRoom, detailTeacher, detailInfo};

                        holder.card4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreateSubstitutionDetailView(detailEntry4);
                            }
                        });
                        break;
                    case 5:
                        //uses to much space
                        if(strHours.length() > 4){
                            holder.hour5.setTextSize(22);
                        }

                        //set data
                        holder.head5.setText(strHead);
                        holder.hour5.setText(strHours);
                        holder.info5.setText(strInfo);

                        //set color
                        holder.head5.setTextColor(Color.parseColor(strColor));
                        holder.hour5.setTextColor(Color.parseColor(strColor));

                        final String[] detailEntry5 = {strHead, strHours, strColor, (detailDate + year),
                                detailClass, detailRoom, detailTeacher, detailInfo};

                        holder.card5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreateSubstitutionDetailView(detailEntry5);
                            }
                        });
                        break;
                }
            }
        }

        //                  !!! FILL THE CARDS WITH DATE-INFORMATION  !!!

        String dateString;
        //null presets don't contain a day TextView
        if (currentViewType > 0 || currentViewType == -1) {
            switch (position) {
                case 0:
                    dateString = "Montag, " + date[0] + year;
                    holder.day.setText(dateString);
                    break;
                case 1:
                    dateString = "Dienstag, " + date[1] + year;
                    holder.day.setText(dateString);
                    break;
                case 2:
                    dateString = "Mittwoch, " + date[2] + year;
                    holder.day.setText(dateString);
                    break;
                case 3:
                    dateString = "Donnerstag, " + date[3] + year;
                    holder.day.setText(dateString);
                    break;
                case 4:
                    dateString = "Freitag, " + date[4] + year;
                    holder.day.setText(dateString);
                    break;
                case 5:
                    dateString = "Montag, " + date[5] + year;
                    holder.day.setText(dateString);
                    break;
                case 6:
                    dateString = "Dienstag, " + date[6] + year;
                    holder.day.setText(dateString);
                    break;
                case 7:
                    dateString = "Mittwoch, " + date[7] + year;
                    holder.day.setText(dateString);
                    break;
                case 8:
                    dateString = "Donnerstag, " + date[8] + year;
                    holder.day.setText(dateString);
                    break;
                case 9:
                    dateString = "Freitag, " + date[9] + year;
                    holder.day.setText(dateString);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return array.size() - 1;
    }

    //detail viewCards for substitution
    public void onCreateSubstitutionDetailView(String[] detail){
        //detail Array: 0 case,  1 hours, 2 color, 3 date, 4 class, 5 room, 6 teacher, 7 info

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View mView = inflater.inflate(R.layout.activity_substitution_details, null);
        TextView txtCase = mView.findViewById(R.id.textViewCase);
        TextView txtHours = mView.findViewById(R.id.textViewHours);
        TextView txtDate = mView.findViewById(R.id.txtDateDetail);
        TextView txtClass = mView.findViewById(R.id.txtClassDetail);
        TextView txtInfo = mView.findViewById(R.id.txtInfoDetail);
        TextView txtTeacher = mView.findViewById(R.id.txtTeacherDetail);
        TextView txtRoom = mView.findViewById(R.id.txtRoomDetail);

        //set handed details
        txtCase.setText(detail[0]);
        txtCase.setTextColor(Color.parseColor(detail[2]));
        txtHours.setText(detail[1]);
        txtHours.setTextColor(Color.parseColor(detail[2]));
        txtDate.setText(detail[3]);
        txtClass.setText(detail[4]);
        txtRoom.setText(detail[5]);
        txtTeacher.setText(detail[6]);
        txtInfo.setText(detail[7]);

        if(detail[0].equals("Nachricht des Tages")){
            txtCase.setText("Nachricht");
        }

        if(detail[7].equals(" ")){
            mView.findViewById(R.id.imgDescription).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewDescription).setVisibility(View.GONE);

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeDescription);
                relative.setVisibility(View.GONE);
            }
        }

        if(detail[6].equals(" ")){
            mView.findViewById(R.id.imgTeacher).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewTeacher).setVisibility(View.GONE);

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeTeacher);
                relative.setVisibility(View.GONE);
            }
        }

        if(detail[5].equals(" ")){
            mView.findViewById(R.id.imgRoom).setVisibility(View.GONE);
            mView.findViewById(R.id.txtViewRoom).setVisibility(View.GONE);

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                RelativeLayout relative = mView.findViewById(R.id.relativeRoom);
                relative.setVisibility(View.GONE);
            }
        }
        if(detail[1].length() > 4){
            txtHours.setTextSize(28);
        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //up-down animation
        dialog.show();

        mView.setOnTouchListener(new OnSwipeTouchListener(context){
            public void onSwipeBottom() {
                dialog.dismiss();
            }
        });
    }

    public class SubstitutionRecyclerViewHolders extends RecyclerView.ViewHolder{

        TextView day, info1, head1, hour1,
                info2, head2, hour2,
                info3, head3, hour3,
                info4, head4, hour4,
                info5, head5, hour5;

        LinearLayout card1, card2, card3, card4, card5;

        public SubstitutionRecyclerViewHolders(View itemView){
            super(itemView);

            day = itemView.findViewById(R.id.txtDay);

            card1 = itemView.findViewById(R.id.linearDefault);
            hour1 = itemView.findViewById(R.id.txtHour);
            head1 = itemView.findViewById(R.id.txtHead);
            info1 = itemView.findViewById(R.id.txtInfoDetail);

            card2 = itemView.findViewById(R.id.linearDefault2);
            hour2 = itemView.findViewById(R.id.txtHour2);
            head2 = itemView.findViewById(R.id.txtHead2);
            info2 = itemView.findViewById(R.id.txtInfoDetail2);

            card3 = itemView.findViewById(R.id.linearDefault3);
            hour3 = itemView.findViewById(R.id.txtHour3);
            head3 = itemView.findViewById(R.id.txtHead3);
            info3 = itemView.findViewById(R.id.txtInfoDetail3);

            card4 = itemView.findViewById(R.id.linearDefault4);
            hour4 = itemView.findViewById(R.id.txtHour4);
            head4 = itemView.findViewById(R.id.txtHead4);
            info4 = itemView.findViewById(R.id.txtInfoDetail4);

            card5 = itemView.findViewById(R.id.linearDefault5);
            hour5 = itemView.findViewById(R.id.txtHour5);
            head5 = itemView.findViewById(R.id.txtHead5);
            info5 = itemView.findViewById(R.id.txtInfoDetail5);
        }
    }
}


