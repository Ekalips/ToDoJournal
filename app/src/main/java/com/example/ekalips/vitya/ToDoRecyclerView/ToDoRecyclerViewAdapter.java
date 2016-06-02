package com.example.ekalips.vitya.ToDoRecyclerView;

/**
 * Created by ekalips on 5/28/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import com.example.ekalips.vitya.MainActivity;
import com.example.ekalips.vitya.R;
import com.example.ekalips.vitya.Task;
import com.example.ekalips.vitya.db.TaskDBHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Locale;


public class ToDoRecyclerViewAdapter extends
        RecyclerView.Adapter<ToDoRecyclerViewAdapter.ViewHolder>  implements ItemTouchHelperAdapter{

    private final OnStartDragListener mDragStartListener;
    private List<Task> mSubjects;
    MainActivity parentActivity;
    TaskDBHelper helper;
    boolean haveDelete = false;
    List<Task> selectedTasks = new ArrayList<>();

    final FloatingActionButton button;

    // Pass in the contact array into the constructor
    public ToDoRecyclerViewAdapter(List<Task> subjects, TaskDBHelper _helper, OnStartDragListener dragStartListener, MainActivity activity) {
        mDragStartListener = dragStartListener;
        mSubjects = subjects;
        this.helper = _helper;
        parentActivity = activity;

        button = new FloatingActionButton(parentActivity);
        button.setColorNormal(Color.RED);
        button.setTitle("Delete");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Task t :
                        selectedTasks) {
                    helper.deleteFromTable(t.id);
                    deleteItem(t);
                }
                parentActivity.floatingActionsMenu.removeButton(button);
                haveDelete = false;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.to_do_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }


    Date date;
    int clickedPos;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Task subject = mSubjects.get(position);
        holder.nameTextView.setText(subject.Task);



        if (mSubjects.get(position).Date.equals("")) {
            holder.reminderTextView.setVisibility(View.GONE);


        } else {

            holder.reminderTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy, HH:MM", Locale.US);
            holder.reminderTextView.setText(dateFormat.format(new Date(mSubjects.get(position).Date)));
        }



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //holder.cardView.setSelected(true);
                    holder.selected = true;
                    selectedTasks.add(mSubjects.get(position));

                    if (!haveDelete) {parentActivity.floatingActionsMenu.addButton(button); haveDelete =true;
                    parentActivity.floatingActionsMenu.expand();}




                    return true;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.selected)
                    {
                        holder.onItemClear();
                        holder.selected = false;
                        selectedTasks.remove(mSubjects.get(position));

                    }

                    else if (selectedTasks.size() > 0)
                    {
                        holder.onItemSelected();
                        holder.selected = true;
                        holder.itemView.setSelected(true);
                        selectedTasks.add(mSubjects.get(position));

                    }
                    if (haveDelete && selectedTasks.size() == 0) {parentActivity.floatingActionsMenu.removeButton(button); haveDelete = false;
                        parentActivity.floatingActionsMenu.collapse();}

                }
            });

//        if (holder.selected) holder.itemView.setSelected(true);
//        else holder.itemView.setSelected(false);


//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//
//                if (holder.selected) {
//                    holder.cardView.setBackgroundColor(Color.TRANSPARENT);
//                    selectedTasks.remove(mSubjects.get(position));
//                    holder.selected = false;
//                }
//                else {
//                    holder.cardView.setBackgroundColor(Color.GRAY);
//                    selectedTasks.add(mSubjects.get(position));
//                    holder.selected = true;
//                }
//
//
//
//                return true;
//            }
//        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.selected)
//                {
//                    holder.cardView.setBackgroundColor(Color.TRANSPARENT);
//                    holder.selected = false;
//                    selectedTasks.remove(mSubjects.get(position));
//                }
//                else if (selectedTasks.size() > 0)
//                {
//                    if (holder.selected) {
//                        holder.cardView.setBackgroundColor(Color.TRANSPARENT);
//                        selectedTasks.remove(mSubjects.get(position));
//                        holder.selected = false;
//                    }
//                    else {
//                        holder.cardView.setBackgroundColor(Color.GRAY);
//                        selectedTasks.add(mSubjects.get(position));
//                        holder.selected = true;
//                    }
//                }
//            }
//        });

    }





    public void addItem(Task task) {
        mSubjects.add(task);
        notifyDataSetChanged();
    }

    public void deleteItem(Task task)
    {
        mSubjects.remove(task);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return (mSubjects != null) ? mSubjects.size() : 0;
    }

    @Override
    public void onItemDismiss(int position) {
        mSubjects.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mSubjects, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public boolean selected;
        public TextView reminderTextView;
        public CardView cardView;
        public LinearLayout selectionLayout;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setClickable(true);
            selected = false;
            cardView = (CardView) itemView.findViewById(R.id.to_do_card_view);
            nameTextView = (TextView) itemView.findViewById(R.id.to_do_text_view_item);
            reminderTextView = (TextView) itemView.findViewById(R.id.remind_textView);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT);
            //cardView.setElevation(5);
        }


    }
}

