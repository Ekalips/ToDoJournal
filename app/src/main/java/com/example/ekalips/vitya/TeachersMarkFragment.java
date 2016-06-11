package com.example.ekalips.vitya;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TeachersMarkFragment extends Fragment {


    List<Student> students;
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
        View parentView = inflater.inflate(R.layout.fragment_bad_marks, container, false);
        students = new ArrayList<>();

        JSONArray array = SQLiteHelper.GetStudents(getContext());
        for (int i = 0;i< array.length();i++)
        {
            try {
                students.add(new Student(array.getJSONObject(i).getString("Name"),array.getJSONObject(i).getString("Surname"),array.getJSONObject(i).getInt("ID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return parentView;
    }
}
