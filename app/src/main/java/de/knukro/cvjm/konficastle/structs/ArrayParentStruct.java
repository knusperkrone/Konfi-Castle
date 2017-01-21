package de.knukro.cvjm.konficastle.structs;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;


public class ArrayParentStruct implements ParentListItem {

    public final CharSequence title;
    public final List<ExpandableDescription> texts;

    public ArrayParentStruct(CharSequence title) {
        this.title = title;
        this.texts = new ArrayList<>();
    }

    @Override
    public List<?> getChildItemList() {
        return texts;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}