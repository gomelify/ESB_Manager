package com.ronschka.david.esb.tabs;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.ronschka.david.esb.R;
import com.ronschka.david.esb.helper.TimetableAdapter;
import com.ronschka.david.esb.helper.Timetable_Item;

import java.util.ArrayList;
import java.util.List;

public class TimetableClass {

    final private Context context;
    final private RecyclerView recyclerView;
    private int cardWidth;
    private StaggeredGridLayoutManager lLayout;

    public TimetableClass(Context context, RecyclerView recyclerView, int spacing, int cardWidth) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.cardWidth = cardWidth;
        List<Timetable_Item> rowListItem = getAllItemList();
        lLayout = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lLayout);

        TimetableAdapter timetableAdapter = new TimetableAdapter(context, rowListItem, cardWidth);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
        recyclerView.setAdapter(timetableAdapter);
    }

    public void rebuild(int cardWidth){
        this.cardWidth = cardWidth;
        recyclerView.setVisibility(View.GONE);
        List<Timetable_Item> rowListItem = getAllItemList();
        recyclerView.setAdapter(new TimetableAdapter(context, rowListItem, cardWidth));
        runLayoutAnimation(recyclerView);
    }

    private List<Timetable_Item> getAllItemList(){

        List<Timetable_Item> allItems = new ArrayList<>();
        allItems.add(new Timetable_Item("SP", "T1", 2, context.getResources().getString(0+ R.color.MaterialCyan)));
        allItems.add(new Timetable_Item("M", "H309", 2, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("E", "H309", 1, context.getResources().getString(0+ R.color.MaterialGreen)));
        allItems.add(new Timetable_Item("E", "H301", 2, context.getResources().getString(0+ R.color.MaterialGreen)));
        allItems.add(new Timetable_Item("M", "E104", 2, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("M", "H309", 1, context.getResources().getString(0+ R.color.MaterialIndigo)));
        allItems.add(new Timetable_Item("S", "E305", 2, context.getResources().getString(0+ R.color.MaterialDeepOrange)));
        allItems.add(new Timetable_Item("WW", "H309", 2, context.getResources().getString(0+ R.color.MaterialPink)));
        allItems.add(new Timetable_Item("PHY", "E302", 2, context.getResources().getString(0+ R.color.MaterialTeal)));
        allItems.add(new Timetable_Item("TI", "H003", 2, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("S", "E202", 2, context.getResources().getString(0+ R.color.MaterialDeepOrange)));
        allItems.add(new Timetable_Item("D", "H301", 2, context.getResources().getString(0+ R.color.MaterialRed)));
        allItems.add(new Timetable_Item("INF", "H113", 1, context.getResources().getString(0+ R.color.MaterialAmber)));
        allItems.add(new Timetable_Item("INF", "H113", 2, context.getResources().getString(0+ R.color.MaterialAmber)));
        allItems.add(new Timetable_Item("REL", "H311", 2, context.getResources().getString(0+ R.color.MaterialPurple)));
        allItems.add(new Timetable_Item("TI", "E204", 2, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("TI", "H113", 1, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("", "", 0, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("GMG", "H309", 2, context.getResources().getString(0+ R.color.MaterialPink)));
        allItems.add(new Timetable_Item("D", "H309", 1, context.getResources().getString(0+ R.color.MaterialRed)));
        allItems.add(new Timetable_Item("", "", 0, context.getResources().getString(0+ R.color.MaterialLightBlue)));
        allItems.add(new Timetable_Item("ET", "E302", 2, context.getResources().getString(0+ R.color.MaterialLightGreen)));

        return allItems;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.grid_animation);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
            outRect.top = space/2;
            outRect.bottom = space/2;
        }
    }
}
