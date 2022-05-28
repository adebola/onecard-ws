package io.factorialsystems.msscprovider.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

@CommonsLog
public class UtilsTest {

    @Test
    public void lastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        log.info(cal);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        int lastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        log.info(lastDayOfMonth);
    }

    @Test
    public void calendarTest() {
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(new Date());

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);

        log.info(String.format("Day of Week %d", dayOfWeek));
        log.info(String.format("Day of Month %d", dayOfMonth));
        log.info(String.format("Week of Year %d", weekOfYear));
        log.info(String.format("Month of Year %d", monthOfYear));
    }
}
