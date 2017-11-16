package com.ronschka.david.esb.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronschka.david.esb.R;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableRecyclerViewHolders> {
    private List<Timetable_Item> itemList;
    private Context context;
    private final int cardWidth;
    final float scale;
    private final View.OnClickListener onClickListener;

    public TimetableAdapter(Context context, final List<Timetable_Item> itemList, int cardWidth, final RecyclerView recyclerView) {
        this.cardWidth = cardWidth;
        this.itemList = itemList;
        this.context = context;
        scale = context.getResources().getDisplayMetrics().density;
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                String[] detail = {itemList.get(itemPosition).getLessonFull(), itemList.get(itemPosition).getColor() ,
                        itemList.get(itemPosition).getRoom(), itemList.get(itemPosition).getTeacher(), getTime(view.getY(), itemList.get(itemPosition).getHours())};
                onCreateTimetableDetailView(detail);
            }
        };
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public TimetableRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_entry, parent, false);
        view.setOnClickListener(onClickListener);
        return new TimetableRecyclerViewHolders(view);
    }

    @Override
    public void onBindViewHolder(TimetableRecyclerViewHolders holder, int position) {

        if(!itemList.get(position).getRoom().isEmpty()) {
            holder.lessonName.setText(itemList.get(position).getLesson());
            holder.roomName.setText(itemList.get(position).getRoom());
            holder.cardView.setCardBackgroundColor(Color.parseColor(itemList.get(position).getColor()));
        }
        else if(!itemList.get(position).getLesson().isEmpty()){ //special cases
            holder.generalInfo.setVisibility(View.VISIBLE);
            holder.lessonName.setVisibility(View.GONE);
            holder.roomName.setVisibility(View.GONE);
            holder.generalInfo.setText(itemList.get(position).getLesson());
            holder.cardView.setCardBackgroundColor(Color.parseColor(itemList.get(position).getColor()));
        }
        else {//there is no hour -> make invisible
            holder.cardView.setVisibility(View.INVISIBLE);
        }

        int hours = itemList.get(position).getHours();
        int size;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            size = 62;
        } else {
            size = 34;
        }

        int pixels = (int) (hours * size * scale + 0.5f);
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.width = cardWidth;
        params.height = pixels;
        holder.cardView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    private void onCreateTimetableDetailView(String[] detail){
        //detail Array: 0 lesson, 1 color, 2 room, 3 teacher, 4 time
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View mView = inflater.inflate(R.layout.activity_timetable_details, null);
        CardView cardTimetable = mView.findViewById(R.id.timetableCardView);
        TextView txtLesson = mView.findViewById(R.id.timetableLesson);
        TextView txtRoom = mView.findViewById(R.id.timetableRoom);
        TextView txtTeacher = mView.findViewById(R.id.timetableTeacher);
        TextView txtTime = mView.findViewById(R.id.timetableTime);

        cardTimetable.setBackgroundColor(Color.parseColor(detail[1]));
        txtLesson.setText(detail[0]);
        txtRoom.setText(detail[2]);
        txtTeacher.setText(detail[3]);
        txtTime.setText(detail[4]);

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

    public class TimetableRecyclerViewHolders extends RecyclerView.ViewHolder{

        public TextView lessonName;
        public TextView roomName;
        public TextView generalInfo;
        public CardView cardView;

        private TimetableRecyclerViewHolders(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_timetable);
            generalInfo = itemView.findViewById(R.id.general_info);
            lessonName = itemView.findViewById(R.id.lesson_name);
            roomName = itemView.findViewById(R.id.room_name);
        }
    }

    private String getTime(float position, int hours){
        int dpPosition = (int) (position / scale + 0.5f);
        int itemSize;   //dp size of item (portrait and land differentiated)
        int startHour;
        String startTime, endTime; //time in minutes

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            itemSize = 62;
        } else {
            itemSize = 34;
        }

        startHour = ((dpPosition - 1) / itemSize) + 1;

        switch(startHour){
            case 1:
                startTime = "8:00";
                break;
            case 2:
                startTime = "8:45";
                break;
            case 3:
                startTime = "9:50";
                break;
            case 4:
                startTime = "10:35";
                break;
            case 5:
                startTime = "11:35";
                break;
            case 6:
                startTime = "12:20";
                break;
            case 7:
                startTime = "13:20";
                break;
            case 8:
                startTime = "14:05";
                break;
            case 9:
                startTime = "15:05";
                break;
            case 10:
                startTime = "15:50";
                break;
            case 11:
                startTime = "17:00";
                break;
            case 12:
                startTime = "18:00";
                break;
            case 13:
                startTime = "18:45";
                break;
            case 14:
                startTime = "19:45";
                break;
            case 15:
                startTime = "20:30";
                break;
            default:
                startTime = "";
                break;
        }

        switch (startHour + hours){
            case 2:
                endTime = "8:45";
                break;
            case 3:
                endTime = "9:30";
                break;
            case 4:
                endTime = "10:35";
                break;
            case 5:
                endTime = "11:20";
                break;
            case 6:
                endTime = "12:20";
                break;
            case 7:
                endTime = "13:05";
                break;
            case 8:
                endTime = "14:05";
                break;
            case 9:
                endTime = "14:50";
                break;
            case 10:
                endTime = "15:50";
                break;
            case 11:
                endTime = "16:35";
                break;
            case 12:
                endTime = "17:45";
                break;
            case 13:
                endTime = "18:45";
                break;
            case 14:
                endTime = "19:30";
                break;
            case 15:
                endTime = "20:30";
                break;
            case 16:
                endTime = "21:15";
                break;
            default:
                endTime = "";
                break;
        }

        return startTime + " - " + endTime + " Uhr";
    }
}