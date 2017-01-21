package de.knukro.cvjm.konficastle.structs;


public class ParsedEvent {
    public final String eventTitle;
    public final String date;
    public final String link;
    public String imagename;
    public final boolean available;

    public ParsedEvent(String eventTitle, String date, String link, boolean available) {
            /*When the title ends with a year, it get's cutted out*/
        this.eventTitle = (eventTitle.matches(".*20[0-9][0-9]$")) ? eventTitle.substring(0, eventTitle.length() - 5) : eventTitle;
        this.date = date;
        this.link = link;
        this.available = available;
    }
}
