package io.factorialsystems.msscprovider.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class Constants {
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final String PAYSTACK_PAY_MODE = "paystack";
    public static final String WALLET_PAY_MODE = "wallet";
    public static final String BULK_RECHARGE = "bulk";
    public static final String SINGLE_RECHARGE = "single";
    public static final String[] ALL_PAYMENT_MODES = {PAYSTACK_PAY_MODE, WALLET_PAY_MODE };
    public static final String HEADER_EMAIL = "email";
    public static final String HEADER_PASSWORD = "password";
    public static final int FIVE_MINUTES = 300;
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MULTIPART_REQUESTPART_NAME = "file";

    public static final BigDecimal MINIMUM_RECHARGE_VALUE = new BigDecimal(10);

}
