package de.knukro.cvjm.konficastle.structs;

import java.util.ArrayList;
import java.util.List;


public class EventSeite {

    //private final String categoryTitle;
    public final List<ParsedEvent> events;

    public EventSeite() {
        //this.categoryTitle = categoryTitle;
        events = new ArrayList<>();
    }
}
