package com.example.ekalips.vitya;



import android.content.Context;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by ekalips on 5/26/16.
 */



   public class MarksAdapter extends
            RecyclerView.Adapter<MarksAdapter.ViewHolder> {

    private List<Subject> mSubjects;
    MainActivity parentActivity;
    ColorGenerator generator;

    // Pass in the contact array into the constructor
    public MarksAdapter(List<Subject> subjects, MainActivity parentActivity) {
        mSubjects = subjects;
        generator = ColorGenerator.MATERIAL;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Subject subject = mSubjects.get(position);
        holder.nameTextView.setText(subject.SubjName);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(subject.SubjName.charAt(0)), generator.getRandomColor());
        holder.imageView.setImageDrawable(drawable);
        if (!PrefsHandler.getBoolean("IsTeacher", false, parentActivity)) {
            holder.marksList.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.marksList.setLayoutManager(new LinearLayoutManager(null, LinearLayoutManager.HORIZONTAL, false));
            holder.marksList.setAdapter(new InnerMarksAdapter(subject.Marks, holder.itemView.getContext(), parentActivity));
        } else {
            holder.line.setVisibility(View.GONE);
            holder.marksList.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("SubjID", subject.SubjID);
                    Fragment fragment = new TeachersMarkFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack("Marks");
                    transaction.replace(R.id.marks_fragment_holder, fragment);
                    transaction.commit();
                    PrefsHandler.setBoolean("TeacherSelected", true, parentActivity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }


    public void updateDataSet(List<Subject> subjects) {
        mSubjects = subjects;
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public RecyclerView marksList;
        public ImageView imageView;
        public LinearLayout line;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.subject_name);
            marksList = (RecyclerView) itemView.findViewById(R.id.marks_list);
            imageView = (ImageView) itemView.findViewById(R.id.circle_image_view);
            line = (LinearLayout) itemView.findViewById(R.id.line_separator);
        }
    }
}

