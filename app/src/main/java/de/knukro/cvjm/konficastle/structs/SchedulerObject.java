package de.knukro.cvjm.konficastle.structs;

import java.util.Date;


public class SchedulerObject {

    public final Date event; //Date when events starts
    public final long length; //How many days the event lasts

    public SchedulerObject(Date event, int length) {
        this.event = event;
        this.length = length;
    }
}
