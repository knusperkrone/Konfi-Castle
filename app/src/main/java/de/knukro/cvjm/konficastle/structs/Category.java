package de.knukro.cvjm.konficastle.structs;

import java.util.ArrayList;
import java.util.List;


public class Category {

    //private final String categoryTitle;
    public final List<ParsedEvent> events;

    public Category() {
        //this.categoryTitle = categoryTitle;
        events = new ArrayList<>();
    }
}
