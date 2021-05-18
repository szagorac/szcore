package com.xenaksys.szcore.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    private final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
    private final static String periodFormatter = "%dm %ds";

    public static String formatTime(long time) {
        Date date = new Date(time);
        return timeFormatter.format(date);
    }

    public static String formatPeriod(long millis) {
        return String.format(periodFormatter,
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }


}
