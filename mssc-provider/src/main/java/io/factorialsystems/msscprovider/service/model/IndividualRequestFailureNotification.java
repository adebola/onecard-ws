package io.factorialsystems.msscprovider.service.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class IndividualRequestFailureNotification {
    private Integer id;
    private String errorMsg;
}
