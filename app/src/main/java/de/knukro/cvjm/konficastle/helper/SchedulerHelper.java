package de.knukro.cvjm.konficastle.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.knukro.cvjm.konficastle.structs.SchedulerObject;

@SuppressWarnings("WeakerAccess")
public class SchedulerHelper {

    @SuppressWarnings("WeakerAccess")
    public static long getSecondOffset(String time, SchedulerObject eventCheck, int day, int offset) {
        Date event = new Date(eventCheck.start.getTime());
        event.setHours(Integer.valueOf(time.substring(0, 2)));
        event.setMinutes(Integer.valueOf(time.substring(3)) - offset);
        event.setDate(event.getDate() + day);

        return (event.getTime() - Calendar.getInstance().getTime().getTime());
    }

    @SuppressWarnings("WeakerAccess")
    public static long getDayDiff(SchedulerObject toCheck) {
        Date now = new Date();
        long diffStart = TimeUnit.DAYS.convert(now.getTime() - toCheck.start.getTime(), TimeUnit.MILLISECONDS);

        long out = -1;

        if (!(diffStart < 0 || diffStart > toCheck.length)) {
            out = diffStart;
        }

        return out;
    }

}
