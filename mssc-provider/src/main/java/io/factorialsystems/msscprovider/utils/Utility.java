package io.factorialsystems.msscprovider.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.Calendar;
import java.util.Date;

public class Utility {
    private static final String[] SHORT_MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private static final char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

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

    public static byte[] hexToBytes(String str) {
        if (str==null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }

    public static Page<?> buildPageRequest(Integer pageNumber, Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return PageHelper.startPage(pageNumber, pageSize);
    }
}
