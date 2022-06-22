package io.factorialsystems.msscwallet.domain.query;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


public class SearchByDateRange {
    private Timestamp fromTs;
    private Timestamp toTs;
    private String userId;

    public Timestamp getFromTs() {
        return fromTs;
    }

    public Timestamp getToTs() {
        return toTs;
    }

    public String getUserId() {
        return userId;
    }

    public void setFromTs(Date fromTs) {
        this.fromTs = new Timestamp(zeroTime(fromTs).getTime());
    }

    public void setToTs(Date toTs) {
        this.toTs = new Timestamp(zeroTime(toTs).getTime());
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private Date zeroTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
