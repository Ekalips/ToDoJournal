package com.example.ekalips.vitya;

import android.support.annotation.Nullable;

/**
 * Created by ekalips on 5/26/16.
 */

public class Task
{
    @Nullable
    public
    String Date;

    public String Task;
    public int id;
    public Task(String _date, String _task, int _id)
    {
        Date = _date; Task = _task; id = _id;
    }
    @Override
    public String toString()
    {
        return  "ID: " + id + "  Task: " + Task + " Date: " + Date.toString();
    }
}
