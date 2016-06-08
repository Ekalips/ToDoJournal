package com.example.ekalips.vitya;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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

    private List<Mark> mSubjects;
    Context context;
    MainActivity parentActivity;
    // Pass in the contact array into the constructor
    public InnerMarksAdapter(List<Mark> subjects, Context context, MainActivity parentAct) {
        mSubjects = subjects;
        Log.d("mSubjects",subjects.toString());
        this.context = context;
        parentActivity = parentAct;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int mark = mSubjects.get(position).mark;
        holder.markTextView.setText(String.valueOf(mark));
        if (mark < 3) holder.markTextView.setTextColor(Color.RED);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context).
                        setTitle("Mark: " + mSubjects.get(position).mark).
                        setMessage("Theme: " + mSubjects.get(position).theme + "\nDate: " + mSubjects.get(position).date)
                        .setPositiveButton("Ok",null);
                if (mSubjects.get(position).mark <= 3) {
                    builder.setNeutralButton("Set reminder", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parentActivity.createEditTextAlert();
                        }
                    });
                }
                builder.create().show();
            }
        });
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

