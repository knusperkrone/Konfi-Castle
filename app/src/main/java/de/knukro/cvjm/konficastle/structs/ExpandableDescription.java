package de.knukro.cvjm.konficastle.structs;


public class ExpandableDescription /* extends ChildListItem */ {

    public final String time; //Necessary to update/delete own notices
    public CharSequence description;

    public ExpandableDescription(CharSequence description, String time) {
        this.description = description;
        this.time = time;
    }


}
