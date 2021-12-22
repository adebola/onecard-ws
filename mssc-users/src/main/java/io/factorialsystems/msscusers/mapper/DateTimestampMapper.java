package io.factorialsystems.msscusers.mapper;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateTimestampMapper {
    public Date asDate(Long ts) {

        if (ts != null) {
            return new Date(ts);
        }

        return null;
    }
}
