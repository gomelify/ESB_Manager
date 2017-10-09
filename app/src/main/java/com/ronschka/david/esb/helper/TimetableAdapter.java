package com.ronschka.david.esb.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ronschka.david.esb.R;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableRecyclerViewHolders> {
    private List<Timetable_Item> itemList;
    private Context context;
    private boolean nullChecker = false;
    private final int cardWidth;

    public TimetableAdapter(Context context, List<Timetable_Item> itemList, int cardWidth) {
        this.cardWidth = cardWidth;
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getHours();
    }

    @Override
    public TimetableRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        nullChecker = false;

        switch(viewType){
            case 0:
                nullChecker = true;
                return new TimetableRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_entry_null, parent, false));
            case 1:
                return new TimetableRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_entry_1, parent, false));
            case 2:
                return new TimetableRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_entry_2, parent, false));
            case 3:
                return new TimetableRecyclerViewHolders(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_entry_2, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(TimetableRecyclerViewHolders holder, int position) {
        if(!nullChecker) {
            holder.lessonName.setText(itemList.get(position).getLesson());
            holder.roomName.setText(itemList.get(position).getRoom());
            holder.cardView.setCardBackgroundColor(Color.parseColor(itemList.get(position).getColor()));

            ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
            params.width = cardWidth;
            holder.cardView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class TimetableRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView lessonName;
        public TextView roomName;
        public CardView cardView;

        public TimetableRecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = itemView.findViewById(R.id.card_view_timetable);
            lessonName = itemView.findViewById(R.id.lesson_name);
            roomName = itemView.findViewById(R.id.room_name);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}