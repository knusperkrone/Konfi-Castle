package de.knukro.cvjm.konficastle.structs;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.LinkedList;
import java.util.List;

import de.knukro.cvjm.konficastle.SharedValues;

public class ExpandableTermin implements Parent<ExpandableDescription> {

    public final String time;
    public final String name;
    public final String group;

    public final LinkedList<ExpandableDescription> details;

    public ExpandableTermin(String time, String name, String group) {
        this.time = time;
        this.name = name;
        this.group = group;
        details = new LinkedList<>(); //Needs at least a empty arrayList
    }

    @Override
    public List<ExpandableDescription> getChildList() {
        return details;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return SharedValues.toExpand.contains(name);
    }


    private int getPosition(String text) {
        int index = 0;
        for (ExpandableDescription expDesrc : this.details) {
            if (text.compareTo(expDesrc.description.toString()) <= 0)
                break;
            index++;
        }
        return index;
    }

    public int insertNotiz(String content) {
        int position = getPosition(SharedValues.NOTIZ_ + content);
        details.add(position, new ExpandableDescription(SharedValues.NOTIZ_ + content, time));
        return position;
    }

    public int removeNotiz(String content) {
        int postition = getPosition(SharedValues.NOTIZ_ + content);
        details.remove(postition);
        return postition;
    }

    public int updateNotiz(String oldContent, String newContent) {
        int postition = getPosition(SharedValues.NOTIZ_ + oldContent);
        details.get(postition).description = SharedValues.NOTIZ_ + newContent;
        return postition;
    }
}