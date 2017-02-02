package de.knukro.cvjm.konficastle.structs;


import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;


public class ArrayParentStruct implements Parent<ExpandableDescription> {

    public final CharSequence title;
    public final List<ExpandableDescription> texts;

    public ArrayParentStruct(CharSequence title) {
        this.title = title;
        this.texts = new ArrayList<>();
    }

    @Override
    public List<ExpandableDescription> getChildList() {
        return texts;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}