package de.knukro.cvjm.konficastle.structs;


public class ExpandableDescription /* extends ChildListItem */ {

    public final String time; //Necessary to update/delete own notices
    public final CharSequence beschreibung;

    public ExpandableDescription(CharSequence s1, String time) {
        beschreibung = s1;
        this.time = time;
    }

}
