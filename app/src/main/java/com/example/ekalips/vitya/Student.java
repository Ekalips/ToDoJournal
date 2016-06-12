package com.example.ekalips.vitya;

/**
 * Created by ekalips on 6/11/16.
 */

public class Student {

    String Name,SName;
    int ID;
    int Mark;
    public Student(String name,String sName, int id)
    {
        Name = name; SName = sName; ID = id; Mark = 0;
    }
    public void setMark(int mark)
    {
        Mark = mark;
    }
    public boolean haveMark()
    {
        return Mark!=0;
    }
}
