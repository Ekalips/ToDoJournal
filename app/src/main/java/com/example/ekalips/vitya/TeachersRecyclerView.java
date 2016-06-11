package com.example.ekalips.vitya;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ekalips on 6/11/16.
 */

public class TeachersRecyclerView extends
        RecyclerView.Adapter<TeachersRecyclerView.ViewHolder> {

    private List<Student> mStudents;
    MainActivity parentActivity;

    // Pass in the contact array into the constructor
    public TeachersRecyclerView(List<Student> subjects,MainActivity parentActivity) {
        mStudents = subjects;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.marks_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student subject = mStudents.get(position);
        holder.nameTextView.setText(subject.Name + "   " + subject.SName);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public RecyclerView marksList;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.subject_name);
            marksList = (RecyclerView) itemView.findViewById(R.id.marks_list);
        }
    }
}

