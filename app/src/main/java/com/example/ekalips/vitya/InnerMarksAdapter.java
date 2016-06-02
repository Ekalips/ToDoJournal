package com.example.ekalips.vitya;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ekalips on 5/26/16.
 */



public class InnerMarksAdapter extends
        RecyclerView.Adapter<InnerMarksAdapter.ViewHolder> {

    private List<Integer> mSubjects;

    // Pass in the contact array into the constructor
    public InnerMarksAdapter(List<Integer> subjects) {
        mSubjects = subjects;
        Log.d("mSubjects",subjects.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.inner_marks_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int mark = mSubjects.get(position);
        holder.markTextView.setText(String.valueOf(mark));
        if (mark < 3) holder.markTextView.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView markTextView;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            markTextView = (TextView) itemView.findViewById(R.id.mark);

        }
    }
}

