package com.example.ekalips.vitya;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekalips on 5/26/16.
 */
public class Subject {
    int SubjID;
    String SubjName;
    List<Mark> Marks = new ArrayList<Mark>();

    public Subject(String SubjName,int SubjID){ this.SubjID = SubjID;this.SubjName = SubjName;}
}
