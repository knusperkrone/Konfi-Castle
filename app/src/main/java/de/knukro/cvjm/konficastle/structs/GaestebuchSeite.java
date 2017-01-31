package de.knukro.cvjm.konficastle.structs;

import java.util.ArrayList;
import java.util.List;


public class GaestebuchSeite {

    //private final String categoryTitle;
    public final List<ParsedEvent> events;

    public GaestebuchSeite() {
        //this.categoryTitle = categoryTitle;
        events = new ArrayList<>();
    }
}
