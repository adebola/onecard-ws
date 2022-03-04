package io.factorialsystems.msscprovider.recharge.ekedp;

import lombok.Getter;

@Getter
public enum Responses {
    SUCCESS("Request Successful"),
    SUCCESS_CODE(0),

    FAILED("Request Unsuccessful"),
    FAILED_CODE(1),

    PROCESSING("Transaction still processing"),
    PROCESSING_CODE(10),

    BAD_INPUT("Bad Input"),
    BAD_INPUT_CODE(102),

    INVALID_SESSION("Invalid session"),
    INVALID_SESSION_CODE_0(220),
    INVALID_SESSION_CODE_1(400),

    INVALID_ACCESS_KEY("Invalid access key."),
    INVALID_ACCESS_KEY_CODE(203),

    INVALID_RECORD("Invalid record"),
    INVALID_RECORD_KEY_CODE(301),

    TRANSACTION_FAILED("Transaction Failed"),
    TRANSACTION_FAILED_CODE(301),

    ORDER_ALREADY_PAID("Order already paid."),
    ORDER_ALREADY_PAID_CODE(307),

    FUND_ERROR("Amount too low"),
    FUND_ERROR_CODE(309),

    NO_RECORD("No Record"),
    NO_RECORD_CODE(310),

    NO_LOGGED_IN("User not logged in"),
    NO_LOGGED_IN_CODE(401),

    INTERNAL_ERROR("Internal System Error"),
    INTERNAL_ERROR_CODE_0(900),
    INTERNAL_ERROR_CODE_1(903),

    GET_ORDER_ERROR("Error fetching transaction details."),
    GET_ORDER_ERROR_CODE(905),

    AUTH_ERROR("Requester Auth Error"),
    AUTH_ERROR_CODE(905);

    private String status;
    private Integer code;
    Responses(String status){
        this.status = status;
    }
    Responses(int code){
        this.code = code;
    }
}
