package io.factorialsystems.msscprovider.utils;

import java.util.Calendar;
import java.util.Date;

public class Utility {
    private static final String[] SHORT_MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public static String getExcelFileNameFromDates(Date startDate, Date endDate) {
        String fileName = null;

        if (startDate == null) return null;

        int m = startDate.getMonth();


        if (endDate == null) {
            fileName = String.format("%d-%s-%d-to-date.xls", startDate.getDate(), SHORT_MONTHS[m], startDate.getYear() + 1900);
        } else {
            int i = endDate.getMonth();

            fileName = String.format("%d-%s-%d-to-%d-%s-%d.xls", startDate.getDate(), SHORT_MONTHS[m], startDate.getYear() + 1900,
                    endDate.getDate(), SHORT_MONTHS[i], endDate.getYear() + 1900);
        }

        return fileName;
    }

    public static Date zeroDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        return calendar.getTime();
    }

    public static Date maxDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR, 23);

        return calendar.getTime();
    }
}
