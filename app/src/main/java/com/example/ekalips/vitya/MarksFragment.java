package com.example.ekalips.vitya;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MarksFragment extends Fragment {
    public MarksFragment() {
    }
RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_marks, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_marks_list);

        Update();

        return rootView;
    }

    public List<Subject> Update()
    {
        JSONArray subjectsJSON = SQLiteHelper.GetSubjects(getContext());
        List<Subject> subjects = new ArrayList<>();
        for (int i = 0;i < subjectsJSON.length();i++)
        {
            try {
                subjects.add(new Subject(subjectsJSON.getJSONObject(i).getString("SubjName"),subjectsJSON.getJSONObject(i).getInt("SubjID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            if(!PrefsHandler.getBoolean("IsTeacher",false,getContext()))
                for (Subject s :
                        subjects) {
                    //Log.d("marks", SQLiteHelper.GetSubjMarksByID(getContext(),PrefsHandler.getInt("ID",-1,getContext()),s.SubjID).toString());
                    JSONArray temp = SQLiteHelper.GetSubjMarksByID(PrefsHandler.getInt("ID",-1,getContext()),s.SubjID,getContext());
                    for (int i = 0;i < temp.length();i++)
                    {
                        s.Marks.add(new Mark(temp.getJSONObject(i).getInt("Mark"),temp.getJSONObject(i).getString("Date"),temp.getJSONObject(i).getString("Theme")));

                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (recyclerView.getAdapter()==null || recyclerView.getLayoutManager()==null) {
            recyclerView.setAdapter(new MarksAdapter(subjects, (MainActivity) getActivity()));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else {
            ((MarksAdapter)recyclerView.getAdapter()).updateDataSet(subjects);
        }
        return subjects;
    }


}
