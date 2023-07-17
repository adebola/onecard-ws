package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RechargeRequestStatusDto {
    private String id;
    private String bulkParentId;
    private String status;
    private String reason;

    public static final String RECHARGE_REQUEST_NOT_FOUND = "Access Denied, Request Not Found";
    public static final String RECHARGE_REQUEST_NOT_OWNER = "Access Forbidden to Request";
    public static final String RECHARGE_REQUEST_SUCCESS = "Request Successful";
    public static final String RECHARGE_REQUEST_FAILED = "Request Failed";
    public static final String RECHARGE_REQUEST_FAILED_AND_REFUNDED = "Request Failed and Refunded";
    public static final String NO_BULK_REQUESTS_FOUND = "No Individual Requests in Bulk Recharge";
}
