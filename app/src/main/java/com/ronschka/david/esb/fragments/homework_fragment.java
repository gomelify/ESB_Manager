package com.ronschka.david.esb.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ronschka.david.esb.AddHomework;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.tabs.HomeworkClass;

public class homework_fragment extends Fragment {
    static HomeworkClass homeworkClass;
    static Spinner spinner;


    public homework_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homework, container, false);

        //spinner homework
        spinner = view.findViewById(R.id.spinnerTabHomework);

        final TextView txtNoHomework = view.findViewById(R.id.txtNoHomework);

        //homework list
        final ListView hwList = view.findViewById(R.id.listViewHomework);

        homeworkClass = new HomeworkClass(getContext(), txtNoHomework, hwList);
        homeworkClass.createHomework(spinner.getSelectedItemPosition());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                homeworkClass.updateHomework(spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Button btnHomework = view.findViewById(R.id.btnAddHomework);
        btnHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddHomework.class));
            }
        });

        return view;
    }

    @Override
    public final boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle() == getString(R.string.dialog_edit)) {
            homeworkClass.editOne(info.position);
            return true;
        }
        if (item.getTitle() == getString(R.string.dialog_delete)) {
            homeworkClass.deleteOne(info.position, spinner.getSelectedItemPosition());
            return true;
        }
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        homeworkClass.updateHomework(spinner.getSelectedItemPosition());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
