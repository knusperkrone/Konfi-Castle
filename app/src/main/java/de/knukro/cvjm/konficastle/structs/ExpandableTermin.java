package de.knukro.cvjm.konficastle.structs;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

import de.knukro.cvjm.konficastle.SharedValues;

public class ExpandableTermin implements ParentListItem {

    public final String time;
    public final String name;
    public final String group;

    public final ArrayList<ExpandableDescription> details;

    public ExpandableTermin(String time, String name, String group) {
        this.time = time;
        this.name = name;
        this.group = group;
        details = new ArrayList<>(); //Needs at least a empty arrayList
    }

    @Override
    public List<?> getChildItemList() {
        return details;
    }

    @Override
    public boolean isInitiallyExpanded() {
        if (SharedValues.toExpand.equals(this.name)) {
            SharedValues.toExpand = "";
            return true;
        }
        return false;
    }

}