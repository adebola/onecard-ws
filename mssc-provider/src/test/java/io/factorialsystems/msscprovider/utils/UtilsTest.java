package io.factorialsystems.msscprovider.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

@CommonsLog
public class UtilsTest {

    @Test
    public void testDate() {
        Date d = new Date();
        Date e = new Date();


        log.info(d);
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        log.info(d);
        log.info(e);

        e.setHours(23);
        e.setMinutes(59);
        e.setSeconds(59);
        log.info(e);
//
//        String file1 = String.format("%d-%d-%d-to-date.xls", d.getDate(), d.getMonth(), d.getYear() + 1900);
//
//        String file2 = String.format("%d-%d-%d-to-%d-%d-%d.xls", d.getDate(), d.getMonth(), d.getYear() + 1900,
//                e.getDate(), e.getMonth(), e.getYear() + 1900);
//
//        log.info(file1);
//        log.info(file2);
    }


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

//    @Test
//    public void mapTest() {
//        Student student = new Student();
//
//        var y = student.getAccount();
//        var z = y.get().getLoan();
//
//        var x = Optional.of(student)
//                .flatMap(Student::getAccount)
//                .flatMap(Account::getLoan)
//                .map(Loan::getAmount)
//                .orElse(0d);
//
//        List<Student> students = Arrays.asList(new Student(), new Student());
//
//        var a = students.stream()
//                .map(Student::getAccount)
//                .map(account -> account.get().getLoan())
//                .map(account -> account.flatMap(Account::getLoan))
//
//    }
}
