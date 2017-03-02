package de.knukro.cvjm.konficastle.structs;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class DynamicTextElement implements Parent<DynamicTextElement> {

    public final CharSequence text;
    public final int type;
    public final ArrayList<DynamicTextElement> childs;

    public DynamicTextElement(CharSequence text, int type) {
        this.text =text;
        this.type = type;
        this.childs = new ArrayList<>();
    }

    @Override
    public List<DynamicTextElement> getChildList() {
        return childs;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
