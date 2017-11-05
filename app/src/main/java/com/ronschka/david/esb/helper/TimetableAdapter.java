package com.ronschka.david.esb.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronschka.david.esb.MainActivity;
import com.ronschka.david.esb.R;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableRecyclerViewHolders> {
    private List<Timetable_Item> itemList;
    private Context context;
    private final int cardWidth;
    final float scale;
    private final View.OnClickListener onClickListener;

    public TimetableAdapter(Context context, final List<Timetable_Item> itemList, int cardWidth, final MainActivity mainActivity, final RecyclerView recyclerView) {
        this.cardWidth = cardWidth;
        this.itemList = itemList;
        this.context = context;
        scale = context.getResources().getDisplayMetrics().density;
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                String[] detail = {itemList.get(itemPosition).getLessonFull(), itemList.get(itemPosition).getColor() ,
                        itemList.get(itemPosition).getRoom(), itemList.get(itemPosition).getTeacher()};
                mainActivity.onCreateTimetableDetailView(detail);
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

    public class TimetableRecyclerViewHolders extends RecyclerView.ViewHolder{

        public TextView lessonName;
        public TextView roomName;
        public TextView generalInfo;
        public CardView cardView;

        public TimetableRecyclerViewHolders(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_timetable);
            generalInfo = itemView.findViewById(R.id.general_info);
            lessonName = itemView.findViewById(R.id.lesson_name);
            roomName = itemView.findViewById(R.id.room_name);
        }
    }
}