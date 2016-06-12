package com.example.ekalips.vitya;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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


public class MarksFragmentHolder extends Fragment {
    public MarksFragmentHolder() {
    }
    RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    Fragment marksListFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_marks_fragment_holder, container, false);

        marksListFragment = new MarksFragment();


        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.marks_fragment_holder,marksListFragment);
        transaction.addToBackStack("InitialMarksList");
        transaction.commit();
        return rootView;
    }
    public void Update()
    {
        ((MarksFragment)marksListFragment).Update();
    }


}
