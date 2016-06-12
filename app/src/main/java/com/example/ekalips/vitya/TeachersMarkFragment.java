package com.example.ekalips.vitya;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class TeachersMarkFragment extends Fragment {


    List<Student> Students;
    public TeachersMarkFragment() {
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
        View parentView = inflater.inflate(R.layout.teachers_mark_fragment, container, false);
        Students = new ArrayList<>();

        JSONArray array = SQLiteHelper.GetStudents(getContext());
        for (int i = 0;i< array.length();i++)
        {
            try {
                Students.add(new Student(array.getJSONObject(i).getString("Name"),array.getJSONObject(i).getString("Surname"),array.getJSONObject(i).getInt("ID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RecyclerView recyclerView = (RecyclerView) parentView.findViewById(R.id.teachers_marks_list);
        TeachersRecyclerViewAdapter adapter = new TeachersRecyclerViewAdapter(Students, (MainActivity) getActivity(),getArguments().getInt("SubjID"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        return parentView;
    }
}
