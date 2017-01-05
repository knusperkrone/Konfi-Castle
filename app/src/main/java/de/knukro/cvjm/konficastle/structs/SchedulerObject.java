package de.knukro.cvjm.konficastle.structs;

import java.util.Date;


public class SchedulerObject {

    public final Date start;
    public final long length;

    public SchedulerObject(Date start, int length) {
        this.start = start;
        this.length = length;
    }
}
