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

import com.ronschka.david.esb.AddExam;
import com.ronschka.david.esb.R;
import com.ronschka.david.esb.tabs.ExamClass;

public class exam_fragment extends Fragment {
    private static ExamClass examClass;
    private static Spinner spinner;

    public exam_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_exam, container, false);

        //spinner homework
        spinner = view.findViewById(R.id.spinnerTabExam);

        final TextView txtNoExams = view.findViewById(R.id.txtNoExams);

        //homework list
        final ListView exList = view.findViewById(R.id.listViewExams);

        examClass = new ExamClass(getContext(), txtNoExams, exList);
        examClass.createExam(spinner.getSelectedItemPosition());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                examClass.updateExams(spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Button btnExam = view.findViewById(R.id.btnAddExam);
        btnExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddExam.class));
            }
        });

        return view;
    }

    @Override
    public final boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle() == getString(R.string.dialog_edit)) {
            examClass.editOne(info.position);
            return true;
        }
        if (item.getTitle() == getString(R.string.dialog_delete)) {
            examClass.deleteOne(info.position, spinner.getSelectedItemPosition());
            return true;
        }

        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        examClass.updateExams(spinner.getSelectedItemPosition());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
